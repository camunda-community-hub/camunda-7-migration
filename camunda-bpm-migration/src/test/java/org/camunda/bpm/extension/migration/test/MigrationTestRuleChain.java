package org.camunda.bpm.extension.migration.test;

import static org.camunda.bpm.extension.migration.test.DummyProcessDeployer.resourceId;

import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class MigrationTestRuleChain implements TestRule {

  @Delegate
  private final RuleChain ruleChain;

  @Getter
  private final ProcessEngineRule processEngineRule;

  @Getter
  private final DummyProcessDeployerRule deployerRule;


  public MigrationTestRuleChain(ProcessEngineRule processEngineRule, DummyProcessDeployerRule deployerRule) {
    this.processEngineRule = processEngineRule;
    this.deployerRule = deployerRule;
    this.ruleChain = RuleChain
      .outerRule(processEngineRule)
      .around(deployerRule);
  }

  public void manageDeployment(String source, BpmnModelInstance... modelInstance) {
    final DeploymentBuilder deploymentBuilder = processEngineRule.getRepositoryService()
      .createDeployment()
      .source(source);

    Stream.of(modelInstance)
      .forEach(m -> deploymentBuilder.addModelInstance(resourceId(m), m));

    processEngineRule.manageDeployment(deploymentBuilder.deploy());
  }
}
