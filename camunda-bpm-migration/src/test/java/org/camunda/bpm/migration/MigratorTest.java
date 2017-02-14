package org.camunda.bpm.migration;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.migration.plan.MigrationPlan;
import org.camunda.bpm.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.migration.plan.step.Step;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.migration.test.DummyProcessDeployer;
import org.camunda.bpm.migration.test.DummyProcessDeployerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.processEngine;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.migration.test.DummyProcessBuilder.build;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class MigratorTest {

	private static final String KEY = MigratorTest.class.getSimpleName();
	private static final String V2 = "2.0";
	private static final ProcessDefinitionSpec SPEC_V2 = ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag(V2).build();
	private static final String V1 = "1.0";
	private static final ProcessDefinitionSpec SPEC_V1 = ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag(V1).build();

	@Rule
	public RuleChain ruleChain = RuleChain
			.outerRule(new ProcessEngineRule())
			.around(new DummyProcessDeployerRule(createDeployer(V1), createDeployer(V2)));

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Migrator migrator;

	private MigrationPlan migrationPlan;

	@Captor
	private ArgumentCaptor<StepExecutionContext> stepExecutionContextCaptor;

	@Mock
	private Step step;

	@Test
	public void exception_when_source_not_found() {
		thrown.expectMessage(allOf(containsString("Source"), containsString("not found")));

		migrationPlan.setFrom(ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag("nonExistentVersion").build());

		migrator.migrate(migrationPlan);
	}

	@Test
	public void exception_when_destination_not_found() {
		thrown.expectMessage(allOf(containsString("Destination"), containsString("not found")));

		migrationPlan.setTo(ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag("nonExistentVersion").build());

		migrator.migrate(migrationPlan);
	}

	@Test
	public void applies_migration_steps() {
		String processInstanceId = startInstance().getId();

		migrationPlan.setSteps(Collections.singletonList(step));
		migrator.migrate(migrationPlan);

		InOrder inOrder = inOrder(step);
		inOrder.verify(step).prepare(any(StepExecutionContext.class));
		inOrder.verify(step).perform(any(StepExecutionContext.class));
	}

	@Test
	public void migrates_active_instances_only() {
		final String completedProcessInstanceId;
		{
			ProcessInstance processInstance = startInstance();
			complete(task(processInstance));
			assertThat(processInstance).isEnded();
			completedProcessInstanceId = processInstance.getId();
		}

		String processInstanceId = startInstance().getId();

		migrationPlan.setSteps(Collections.singletonList(step));
		migrator.migrate(migrationPlan);

		InOrder inOrder = inOrder(step);
		inOrder.verify(step).prepare(stepExecutionContextCaptor.capture());
		inOrder.verify(step).perform(stepExecutionContextCaptor.capture());
		inOrder.verifyNoMoreInteractions();

		Set<String> processInstanceIds = stepExecutionContextCaptor.getAllValues().stream()
				.map(StepExecutionContext::getProcessInstanceId)
				.collect(Collectors.toSet());
		assertThat(processInstanceIds).hasSize(1).contains(processInstanceId);
	}

	@Test
	public void uses_all_() {
		String processInstanceId = startInstance().getId();

		migrationPlan.setSteps(Collections.singletonList(step));
		migrator.migrate(migrationPlan);
	}

	@Before
	public void setup_stuff() {
		migrator = new Migrator(processEngine());

		migrationPlan = MigrationPlan.builder()
				.from(SPEC_V1)
				.to(SPEC_V2)
				.build();
	}

	private DummyProcessDeployer.DummyProcessDeployerBuilder createDeployer(String version) {
		return DummyProcessDeployer.builder()
				.source(getClass().getSimpleName())
				.model(build(KEY, version));
	}

	private ProcessInstance startInstance() {
		ProcessDefinition processDefinition = repositoryService().createProcessDefinitionQuery().processDefinitionKey(KEY).versionTag(V1).singleResult();
		return runtimeService().startProcessInstanceById(processDefinition.getId());
	}
}
