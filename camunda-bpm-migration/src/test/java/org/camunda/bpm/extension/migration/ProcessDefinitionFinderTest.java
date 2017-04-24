package org.camunda.bpm.extension.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService;
import static org.camunda.bpm.extension.migration.test.DummyProcessBuilder.build;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.migration.plan.DeploymentSpec;
import org.camunda.bpm.extension.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.extension.migration.test.DummyProcessDeployer;
import org.camunda.bpm.extension.migration.test.DummyProcessDeployerRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class ProcessDefinitionFinderTest {

  private static final String KEY_A = ProcessDefinitionFinderTest.class.getSimpleName() + "A";
  private static final String KEY_B = ProcessDefinitionFinderTest.class.getSimpleName() + "B";
  private static final String TAG_V1 = "v1";
  private static final String TAG_V2 = "v2";
  private static final String SOURCE_1 = "source1";
  private static final String SOURCE_2 = "source2";
  private static final DummyProcessDeployer.DummyProcessDeployerBuilder DEPLOYER_BUILDER = DummyProcessDeployer.builder()
    .source(SOURCE_1)
    .model(build(KEY_A, TAG_V1))
    .model(build(KEY_B, TAG_V2));

  @Rule
  public RuleChain ruleChain = RuleChain
    .outerRule(new ProcessEngineRule())
    .around(new DummyProcessDeployerRule(DEPLOYER_BUILDER));

  private ProcessDefinitionFinder finder = new ProcessDefinitionFinder(repositoryService());

  @Test
  public void find_by_processDefinitionKey() {
    ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().processDefinitionKey(KEY_B).build();
    Optional<ProcessDefinition> processDefinition = finder.find(spec);
    assertThat(processDefinition.isPresent()).isTrue();
  }

  @Test
  public void find_by_versionTag() {
    ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().versionTag(TAG_V1).build();
    Optional<ProcessDefinition> processDefinition = finder.find(spec);
    assertThat(processDefinition.isPresent()).isTrue();
  }

  @Test
  public void find_by_version() {
    runWithAnotherDeployment(() -> {
      ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().processDefinitionVersion(2).build();
      Optional<ProcessDefinition> processDefinition = finder.find(spec);
      assertThat(processDefinition.isPresent()).isTrue();
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

      Optional<ProcessDefinition> processDefinition = finder.find(with(deploymentSpec));
      assertThat(processDefinition.isPresent()).overridingErrorMessage("ProcessDefinition not found for %s", deploymentSpec).isTrue();
    });
  }

  @Test
  public void find_by_deploymentSpec_source() {
    runWithAnotherDeployment(() -> {
      DeploymentSpec deploymentSpec = DeploymentSpec.builder()
        .source(SOURCE_2)
        .build();

      Optional<ProcessDefinition> processDefinition = finder.find(with(deploymentSpec));
      assertThat(processDefinition.isPresent()).isTrue();
    });
  }

  @Test
  public void empty_result_when_deploymentSpec_has_no_match() {
    runWithAnotherDeployment(() -> {
      DeploymentSpec deploymentSpec = DeploymentSpec.builder()
        .source("nonExistentSource")
        .build();

      Optional<ProcessDefinition> processDefinition = finder.find(with(deploymentSpec));
      assertThat(processDefinition.isPresent()).isFalse();
    });
  }

  private ProcessDefinitionSpec with(DeploymentSpec deploymentSpec) {
    return ProcessDefinitionSpec.builder().deploymentSpec(deploymentSpec).build();
  }

  private void runWithAnotherDeployment(Runnable testFunction) {
    DummyProcessDeployer deployer2 = DummyProcessDeployer.builder().repositoryService(repositoryService())
      .source(SOURCE_2)
      .model(build(KEY_A, TAG_V2))
      .build();
    deployer2.deploy();

    try {
      testFunction.run();
    } finally {
      deployer2.undeploy();
    }
  }
}
