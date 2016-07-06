package org.camunda.bpm.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.migration.test.DummyProcessBuilder.build;

import java.time.ZonedDateTime;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.migration.plan.DeploymentSpec;
import org.camunda.bpm.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.migration.test.DummyProcessDeployer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProcessDefinitionFinderTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    private ProcessDefinitionFinder finder;

    private DummyProcessDeployer deployer;

    @Before
    public void deploy() {
        finder  = new ProcessDefinitionFinder(processEngineRule.getRepositoryService());
        deployer = DummyProcessDeployer.builder().repositoryService(processEngineRule.getRepositoryService())
                .source(getClass().getSimpleName())
                .model(build("foo", "v2.0"))
                .model(build("bar", "v1.0"))
                .build();
        deployer.deploy();
    }

    @After
    public void undeploy() {
        deployer.undeploy();
    }

    @Test
    public void find_by_processDefinitionKey() {
        ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().processDefinitionKey("foo").build();
        ProcessDefinition processDefinition = finder.find(spec);
        assertThat(processDefinition).isNotNull();
    }

    @Test
    public void find_by_versionTag() {
        ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().versionTag("v1.0").build();
        ProcessDefinition processDefinition = finder.find(spec);
        assertThat(processDefinition).isNotNull();
    }

    @Test
    public void find_by_version() {
        runWithAnotherDeployment(() -> {
            ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().version(2).build();
            ProcessDefinition processDefinition = finder.find(spec);
            assertThat(processDefinition).isNotNull();
        });
    }

    @Test
    public void find_by_deploymentSpec_earliest_latest() {
        ZonedDateTime beforeDeployment = ZonedDateTime.now();

        runWithAnotherDeployment(() -> {
            DeploymentSpec deploymentSpec = DeploymentSpec.builder()
                    .earliestDeployment(beforeDeployment)
                    .latestDeployment(ZonedDateTime.now())
                    .build();

            ProcessDefinition processDefinition = finder.find(with(deploymentSpec));

            assertThat(processDefinition).isNotNull();
        });
    }

    @Test
    public void find_by_deploymentSpec_source() {
        runWithAnotherDeployment(() -> {
            DeploymentSpec deploymentSpec = DeploymentSpec.builder()
                    .source("bar")
                    .build();

            ProcessDefinition processDefinition = finder.find(with(deploymentSpec));

            assertThat(processDefinition).isNotNull();
        });
    }

    private ProcessDefinitionSpec with(DeploymentSpec deploymentSpec) {
        return ProcessDefinitionSpec.builder().deploymentSpec(deploymentSpec).build();
    }

    private void runWithAnotherDeployment(Runnable testFunction) {
        DummyProcessDeployer deployer2 = DummyProcessDeployer.builder().repositoryService(processEngineRule.getRepositoryService())
                .source("bar")
                .model(build("foo", "v2.0"))
                .build();
        deployer2.deploy();

        try {
            testFunction.run();
        } finally {
            deployer2.undeploy();
        }
    }
}