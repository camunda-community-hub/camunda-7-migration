package org.camunda.bpm.extension.migration;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.processEngine;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.extension.migration.test.DummyProcessBuilder.build;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ResourceDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.extension.migration.plan.MigrationPlan;
import org.camunda.bpm.extension.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.extension.migration.plan.step.Step;
import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.extension.migration.test.DummyProcessDeployer;
import org.camunda.bpm.extension.migration.test.DummyProcessDeployerRule;
import org.camunda.bpm.extension.migration.test.MigrationTestRuleChain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MultipleRevisionsMigratorTest {

  private static final String KEY = MultipleRevisionsMigratorTest.class.getSimpleName();
  private static final String SOURCE = MultipleRevisionsMigratorTest.class.getSimpleName();

  private static final String V1 = "1.0";
  private static final String V2 = "2.0";

  private static final ProcessDefinitionSpec SPEC_V2 = ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag(V2).build();
  private static final ProcessDefinitionSpec SPEC_V1 = ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag(V1).build();

  @Rule
  public final MigrationTestRuleChain ruleChain = new MigrationTestRuleChain(
    new ProcessEngineRule(),
    new DummyProcessDeployerRule(
      createDeployer(V1),
      createDeployer(V2)
    )
  );

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Migrator migrator;

  @Captor
  private ArgumentCaptor<StepExecutionContext> stepExecutionContextCaptor;

  @Mock
  private Step step;


  @Before
  public void setup() {
    migrator = new Migrator(processEngine());
  }

  @Test
  public void can_start_processV1_in_two_revisions() {
    // start first in revision 1
    String pi1 = startInstance().getId();

    // deploy new revision
    ruleChain.manageDeployment(SOURCE, build(KEY, V1));

    String pi2 = startInstance().getId();

    ProcessDefinition pd1 = processDefinition(pi1);
    ProcessDefinition pd2 = processDefinition(pi2);

    Assertions.assertThat(pd1.getVersionTag()).isEqualTo(V1);
    Assertions.assertThat(pd2.getVersionTag()).isEqualTo(V1);

    Assertions.assertThat(pd1.getVersion()).isLessThan(pd2.getVersion());
  }

  @Test
  public void migrates_two_instances_with_same_version_but_different_revisions() {
    // start first V1
    String pi1 = startInstance().getId();

    // deploy new revision
    ruleChain.manageDeployment(SOURCE, build(KEY, V1));

    // start second V1 - new revision
    String pi2 = startInstance().getId();

    Assertions.assertThat(processDefinition(pi1).getVersionTag()).isEqualTo(V1);
    Assertions.assertThat(processDefinition(pi2).getVersionTag()).isEqualTo(V1);

    MigrationPlan migrationPlan = MigrationPlan.builder()
      .from(SPEC_V1)
      .to(SPEC_V2)
      .step(step)
      .build();

    migrator.migrate(migrationPlan);

    // have been migrated
    Assertions.assertThat(processDefinition(pi1).getVersionTag()).isEqualTo(V2);
    Assertions.assertThat(processDefinition(pi2).getVersionTag()).isEqualTo(V2);


  }

  private int getRevision(String processInstanceId) {
    return processDefinition(processInstanceId).getVersion();
  }

  private DummyProcessDeployer.DummyProcessDeployerBuilder createDeployer(String version) {
    return DummyProcessDeployer.builder()
      .source(SOURCE)
      .model(build(KEY, version));
  }

  private ProcessDefinition processDefinition(String processInstanceId) {
    String pdId = runtimeService().createProcessInstanceQuery().active().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
    return repositoryService().createProcessDefinitionQuery().processDefinitionId(pdId).singleResult();
  }

  private ProcessInstance startInstance() {
    ProcessDefinition processDefinition = repositoryService().createProcessDefinitionQuery()
      .processDefinitionKey(KEY).versionTag(V1).list()
      .stream()
      .sorted(Comparator.comparingInt(ResourceDefinition::getVersion).reversed())
      .findFirst().get();
    return runtimeService().startProcessInstanceById(processDefinition.getId());
  }

}
