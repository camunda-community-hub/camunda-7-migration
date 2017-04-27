package org.camunda.bpm.extension.migration.plan.step.variable;

import org.camunda.bpm.extension.migration.plan.step.Step;
import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.extension.migration.plan.step.variable.strategy.DeleteStrategy;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Deletes a variable.
 */
@Builder
public class VariableDeleteStep implements Step {

  @NonNull
  private DeleteStrategy deleteStrategy;

  @Setter
  @Getter
  @NonNull
  private String variableName;

  @Override
  public void perform(StepExecutionContext context) {
    deleteStrategy.remove(context, variableName);
  }
}
