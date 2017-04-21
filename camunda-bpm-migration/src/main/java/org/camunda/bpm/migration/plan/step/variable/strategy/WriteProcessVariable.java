package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import lombok.extern.slf4j.Slf4j;

/**
 * A {@link WriteStrategy} that writes a process variable.
 */
@Slf4j
public class WriteProcessVariable extends AbstractReadWriteStrategy implements WriteStrategy {

  @Override
  public void write(StepExecutionContext stepExecutionContext, String variableName, TypedValue value) {
    log.info("writing process variable {} with {}", variableName, value);
    getRuntimeService(stepExecutionContext)
      .setVariable(stepExecutionContext.getProcessInstanceId(), variableName, value);
  }
}
