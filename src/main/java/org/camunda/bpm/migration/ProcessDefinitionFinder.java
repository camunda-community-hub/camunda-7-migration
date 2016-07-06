package org.camunda.bpm.migration;

import java.time.Instant;
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
import org.camunda.bpm.migration.plan.DeploymentSpec;
import org.camunda.bpm.migration.plan.ProcessDefinitionSpec;


public class ProcessDefinitionFinder {

    private final RepositoryService repositoryService;

    public ProcessDefinitionFinder(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    ProcessDefinition find(ProcessDefinitionSpec spec) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if(spec.getProcessDefinitionKey() != null) {
            processDefinitionQuery.processDefinitionKey(spec.getProcessDefinitionKey());
        }
        if(spec.getVersionTag() != null) {
            processDefinitionQuery.versionTag(spec.getVersionTag());
        }
        if(spec.getVersion() != null) {
            processDefinitionQuery.processDefinitionVersion(spec.getVersion());
        }
        if(spec.getDeploymentSpec() != null) {
            findDeployment(spec.getDeploymentSpec()).ifPresent(
                    deployment -> processDefinitionQuery.deploymentId(deployment.getId())
            );
            findDeployment(spec.getDeploymentSpec()).ifPresent(
                    deployment -> System.out.println("SRC "+deployment.getSource())
            );
        }

        return processDefinitionQuery.singleResult();
    }

    private Optional<Deployment> findDeployment(DeploymentSpec spec) {
        return Optional.ofNullable(createDeploymentQuery(spec).singleResult());
    }

    private DeploymentQuery createDeploymentQuery(DeploymentSpec spec) {
        DeploymentQuery query = repositoryService.createDeploymentQuery();

        Function<ZonedDateTime, Instant> toInstant = ZonedDateTime::toInstant;

        add(query, DeploymentQuery::deploymentAfter, spec.getEarliestDeployment(), ZonedDateTime::toInstant);

        if(spec.getEarliestDeployment() != null ) {
            query.deploymentAfter(Date.from(spec.getEarliestDeployment().toInstant()));
        }
        if(spec.getLatestDeployment() != null) {
            query.deploymentBefore(Date.from(spec.getLatestDeployment().toInstant()));
        }
        if(spec.getSource() != null) {
            query.deploymentSource(spec.getSource());
        }
        return query;
    }

    private void add(DeploymentQuery query, BiFunction<DeploymentQuery, Date, DeploymentQuery> setter,
                     ZonedDateTime earliestDeployment, Function<ZonedDateTime, Instant> conversion) {

    }


}
