package org.camunda.bpm.extension.migration;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.extension.migration.plan.DeploymentSpec;
import org.camunda.bpm.extension.migration.plan.ProcessDefinitionSpec;


public class ProcessDefinitionFinder {

  private final RepositoryService repositoryService;

  public ProcessDefinitionFinder(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  public Optional<ProcessDefinition> find(ProcessDefinitionSpec spec) {
    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

    add(processDefinitionQuery, ProcessDefinitionQuery::processDefinitionKey, spec.getProcessDefinitionKey());
    add(processDefinitionQuery, ProcessDefinitionQuery::versionTag, spec.getVersionTag());
    add(processDefinitionQuery, ProcessDefinitionQuery::processDefinitionVersion, spec.getProcessDefinitionVersion());

    return Optional.ofNullable(spec.getDeploymentSpec())
      .flatMap(this::findDeployment)
      .map(Deployment::getId)
      .map(processDefinitionQuery::processDefinitionId)
      .map(ProcessDefinitionQuery::singleResult);
  }

  private Optional<Deployment> findDeployment(DeploymentSpec spec) {
    return Optional.ofNullable(createDeploymentQuery(spec).singleResult());
  }

  private DeploymentQuery createDeploymentQuery(DeploymentSpec spec) {
    DeploymentQuery query = repositoryService.createDeploymentQuery();

    Function<ZonedDateTime, Date> toDate = chain(ZonedDateTime::toInstant, Date::from);
    add(query, DeploymentQuery::deploymentAfter, spec.getEarliestDeployment(), toDate);
    add(query, DeploymentQuery::deploymentBefore, spec.getLatestDeployment(), toDate);
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
