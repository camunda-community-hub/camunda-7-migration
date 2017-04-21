package org.camunda.bpm.migration.plan.step.variable.strategy;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.Map;
import java.util.Optional;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.type.ValueType;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.test.ProcessV1;
import org.junit.Test;

@Deployment(resources = ProcessV1.BPMN_FILE)
public class ReadTaskVariableTest extends AbstractStrategyTest {

  private ReadTaskVariable strategy = new ReadTaskVariable(ProcessV1.TASK_DEFINITION_KEY);

  @Test
  public void read_should_read_task_variable() {
    Optional<TypedValue> value = strategy.read(context, "FOO");

    assertThat(value).isNotNull();
    assertThat(value.isPresent());
    assertThat(value.get().getValue()).isEqualTo("TASK-BAR");
    assertThat(value.get().getType()).isEqualTo(ValueType.STRING);
  }

  @Test
  public void read_should_not_change_var_on_read() {
    strategy.read(context, "FOO");

    Map<String, Object> variables = taskService().getVariablesLocal(taskId);
    assertThat(variables).hasSize(1).containsEntry("FOO", "TASK-BAR");
  }

  @Test
  public void remove_should_remove_task_variable() {
    strategy.remove(context, "FOO");

    Map<String, Object> variables = taskService().getVariablesLocal(taskId);
    assertThat(variables).isEmpty();
  }

  @Test
  public void remove_should_not_remove_process_variable() {
    strategy.remove(context, "FOO");

    Map<String, Object> variables = runtimeService().getVariables(context.getProcessInstanceId());
    assertThat(variables).hasSize(1);
  }
}
