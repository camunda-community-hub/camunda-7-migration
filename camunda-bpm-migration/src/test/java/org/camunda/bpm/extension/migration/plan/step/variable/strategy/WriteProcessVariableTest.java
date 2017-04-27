package org.camunda.bpm.extension.migration.plan.step.variable.strategy;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.extension.migration.test.ProcessV1;
import org.junit.Test;

@Deployment(resources = ProcessV1.BPMN_FILE)
public class WriteProcessVariableTest extends AbstractStrategyTest {

  private WriteProcessVariable strategy = new WriteProcessVariable();

  @Test
  public void should_write_process_variable() {
    strategy.write(context, "FOO", NEW_VAL);

    TypedValue value = runtimeService().getVariableTyped(context.getProcessInstanceId(), "FOO");
    assertThat(value).isNotNull();
    assertThat(value.getValue()).isEqualTo(42L);
    assertThat(value.getType()).isEqualTo(NEW_VAL.getType());
  }

  @Test
  public void should_write_null_value() {
    strategy.write(context, "FOO", NULL_VAL);

    TypedValue value = runtimeService().getVariableTyped(context.getProcessInstanceId(), "FOO");
    assertThat(value).isNotNull();
    assertThat(value.getValue()).isNull();
    assertThat(value.getType()).isEqualTo(NULL_VAL.getType());
  }
}
