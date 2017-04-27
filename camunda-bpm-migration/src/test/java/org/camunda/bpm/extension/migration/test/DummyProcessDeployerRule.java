package org.camunda.bpm.extension.migration.test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class DummyProcessDeployerRule extends TestWatcher {

  private final List<DummyProcessDeployer.DummyProcessDeployerBuilder> deployerBuilders;
  private List<DummyProcessDeployer> deployers;

  public DummyProcessDeployerRule(DummyProcessDeployer.DummyProcessDeployerBuilder... deployerBuilders) {
    this.deployerBuilders = Arrays.asList(deployerBuilders);
  }

  @Override
  protected void starting(Description description) {
    deployers = deployerBuilders.stream()
      .map(builder -> builder.repositoryService(repositoryService()).build())
      .collect(Collectors.toList());
    deployers.forEach(DummyProcessDeployer::deploy);
  }

  @Override
  protected void finished(Description description) {
    deployers.forEach(deployer -> {
      runtimeService().createProcessInstanceQuery().deploymentId(deployer.getDeployment().getId()).list()
        .forEach(processInstance ->
          runtimeService().deleteProcessInstance(processInstance.getId(), "teardown"));
      deployer.undeploy();
    });
  }
}
