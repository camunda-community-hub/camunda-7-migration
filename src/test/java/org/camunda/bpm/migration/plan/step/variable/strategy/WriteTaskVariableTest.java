package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.test.ProcessV1;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

@Deployment(resources = ProcessV1.BPMN_FILE)
public class WriteTaskVariableTest extends AbstractStrategyTest {

	private WriteProcessVariable strategy = new WriteProcessVariable();

	@Test
	public void write_should_write_task_variable() {
		strategy.write(context, "FOO", NEW_VAL);

		TypedValue value = runtimeService().getVariableTyped(context.getProcessInstanceId(), "FOO");
		assertThat(value).isNotNull();
		assertThat(value.getValue()).isEqualTo(42L);
		assertThat(value.getType()).isEqualTo(ValueType.LONG);
	}
}
