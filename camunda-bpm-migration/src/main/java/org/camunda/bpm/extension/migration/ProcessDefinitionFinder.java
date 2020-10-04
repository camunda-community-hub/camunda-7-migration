package org.camunda.bpm.extension.migration;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.extension.migration.plan.DeploymentSpec;
import org.camunda.bpm.extension.migration.plan.ProcessDefinitionSpec;

@RequiredArgsConstructor
public class ProcessDefinitionFinder {

  private final RepositoryService repositoryService;

  public Optional<ProcessDefinition> findOne(ProcessDefinitionSpec spec) {
    ProcessDefinitionQuery processDefinitionQuery = prepareProcessDefinitionQuery(spec);

    if (spec.getDeploymentSpec() != null) {
      Optional<Deployment> deployment = findDeployment(spec.getDeploymentSpec());
      if (!deployment.isPresent()) {
        return Optional.empty();
      } else {
        add(processDefinitionQuery, ProcessDefinitionQuery::deploymentId, deployment.get(), Deployment::getId);
      }
    }

    return Optional.ofNullable(processDefinitionQuery.singleResult());
  }


  public List<ProcessDefinition> findAll(ProcessDefinitionSpec spec) {
    ProcessDefinitionQuery processDefinitionQuery = prepareProcessDefinitionQuery(spec);

    if (spec.getDeploymentSpec() != null) {
      Optional<Deployment> deployment = findDeployment(spec.getDeploymentSpec());
      if (!deployment.isPresent()) {
        return Collections.emptyList();
      } else {
        add(processDefinitionQuery, ProcessDefinitionQuery::deploymentId, deployment.get(), Deployment::getId);
      }
    }

    return processDefinitionQuery.list();
  }


  private ProcessDefinitionQuery prepareProcessDefinitionQuery(ProcessDefinitionSpec spec) {
    final ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

    add(processDefinitionQuery, ProcessDefinitionQuery::processDefinitionKey, spec.getProcessDefinitionKey());
    add(processDefinitionQuery, ProcessDefinitionQuery::versionTag, spec.getVersionTag());
    add(processDefinitionQuery, ProcessDefinitionQuery::processDefinitionVersion, spec.getProcessDefinitionVersion());

    return processDefinitionQuery;
  }


  private Optional<Deployment> findDeployment(DeploymentSpec spec) {
    return Optional.ofNullable(createDeploymentQuery(spec).singleResult());
  }

  private DeploymentQuery createDeploymentQuery(DeploymentSpec spec) {
    DeploymentQuery query = repositoryService.createDeploymentQuery();

    Function<ZonedDateTime, Date> toInstant = chain(ZonedDateTime::toInstant, Date::from);
    add(query, DeploymentQuery::deploymentAfter, spec.getEarliestDeployment(), toInstant);
    add(query, DeploymentQuery::deploymentBefore, spec.getLatestDeployment(), toInstant);
    add(query, DeploymentQuery::deploymentSource, spec.getSource());
    add(query, DeploymentQuery::deploymentName, spec.getName());
    add(query, DeploymentQuery::tenantIdIn, spec.getTenantId(), s -> new String[]{s});

    return query;
  }

  private <S> void add(DeploymentQuery query,
                       BiFunction<DeploymentQuery, S, DeploymentQuery> setter,
                       S specValue) {
    add(query, setter, specValue, Function.identity());
  }

  private <S, Q> void add(DeploymentQuery query,
                          BiFunction<DeploymentQuery, Q, DeploymentQuery> setter,
                          S specValue,
                          Function<S, Q> conversion) {
    if (specValue != null)
      setter.apply(query, conversion.apply(specValue));
  }

  private <S> void add(ProcessDefinitionQuery query,
                       BiFunction<ProcessDefinitionQuery, S, ProcessDefinitionQuery> setter,
                       S specValue) {
    add(query, setter, specValue, Function.identity());
  }

  private <S, Q> void add(ProcessDefinitionQuery query,
                          BiFunction<ProcessDefinitionQuery, Q, ProcessDefinitionQuery> setter,
                          S specValue,
                          Function<S, Q> conversion) {
    if (specValue != null)
      setter.apply(query, conversion.apply(specValue));
  }

  private <A, B, C> Function<A, C> chain(Function<A, B> f1, Function<B, C> f2) {
    return f1.andThen(f2);
  }

}
