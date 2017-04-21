package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.migration.plan.step.StepExecutionContext;

/**
 * A DeleteStrategy defines how a {@link org.camunda.bpm.migration.plan.step.variable.VariableDeleteStep} removes
 * variables.
 */
public interface DeleteStrategy {

  /**
   * Removes a variable. This is used in the context of renaming variables.
   * <p>
   * This method is placed in the <strong>read</strong> interface because it's the source variable that is to be deleted.
   * {@link WriteStrategy}s might not know about the source variable and are therefore unable to delete it.
   *
   * @param stepExecutionContext the context of this strategy's execution
   * @param variableName         the variable to delete
   */
  void remove(StepExecutionContext stepExecutionContext, String variableName);
}
