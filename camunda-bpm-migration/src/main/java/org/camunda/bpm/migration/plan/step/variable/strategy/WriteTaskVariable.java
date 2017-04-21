package org.camunda.bpm.migration.plan.step.variable.strategy;

import java.util.Optional;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link WriteStrategy} that writes a task variable. The task is identified by its TaskDefinitionKey.
 */
@RequiredArgsConstructor
@Slf4j
public class WriteTaskVariable extends AbstractReadWriteStrategy implements WriteStrategy {

  @NonNull
  private final String taskDefinitionKey;

  @Override
  public void write(StepExecutionContext stepExecutionContext, String variableName, TypedValue value) {
    Optional<String> taskExecutionId = getTaskId(stepExecutionContext, taskDefinitionKey);
    taskExecutionId.ifPresent(
      id -> {
        log.info("writing task variable {} with value {} to {} (ID:{})", variableName, value, taskDefinitionKey, id);
        getTaskService(stepExecutionContext).setVariableLocal(id, variableName, value);
      }
    );
  }

}
