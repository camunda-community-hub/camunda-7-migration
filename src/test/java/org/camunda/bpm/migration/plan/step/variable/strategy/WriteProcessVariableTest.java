package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.test.ProcessV1;
import org.junit.Test;

import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

@Deployment(resources = ProcessV1.BPMN_FILE)
public class WriteProcessVariableTest extends AbstractStrategyTest {

	private WriteTaskVariable strategy = new WriteTaskVariable(ProcessV1.TASK_DEFINITION_KEY);

	@Test
	public void write_should_write_task_variable() {
		strategy.write(context, "FOO", NEW_VAL);

		TypedValue value = taskService().getVariableLocalTyped(taskId, "FOO");
		assertThat(value).isNotNull();
		assertThat(value.getValue()).isEqualTo(42L);
		assertThat(value.getType()).isEqualTo(ValueType.LONG);
	}

	@Test
	public void write_should_not_write_process_variable() {
		strategy.write(context, "FOO", NEW_VAL);

		Map<String, Object> variables = runtimeService().getVariables(context.getProcessInstanceId());
		assertThat(variables).hasSize(1).containsEntry("FOO","BAR");
	}

}
