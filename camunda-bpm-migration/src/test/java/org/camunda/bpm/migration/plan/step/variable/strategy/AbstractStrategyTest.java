package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.LongValue;
import org.camunda.bpm.engine.variable.value.StringValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.migration.test.ProcessV1;
import org.junit.Before;
import org.junit.Rule;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.processEngine;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

public class AbstractStrategyTest {

	protected static final LongValue NEW_VAL = Variables.longValue(42L);
	protected static final StringValue NULL_VAL = Variables.stringValue(null);

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule();

	protected StepExecutionContext context;

	protected String taskId;

	@Before
	public void startProcess() {
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(ProcessV1.PROCESS_DEFINITION_KEY);
		runtimeService().setVariable(processInstance.getProcessInstanceId(), "FOO", "BAR");
		taskId = task(processInstance).getId();
		taskService().setVariableLocal(taskId, "FOO", "TASK-BAR");

		context = StepExecutionContext.builder()
				.processEngine(processEngine())
				.processInstanceId(processInstance.getId())
				.build();
	}
}
