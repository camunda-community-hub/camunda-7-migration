package org.camunda.bpm.migration.plan.step.variable.strategy;

import java.util.Optional;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

/**
 * A ReadStrategy defines how a {@link org.camunda.bpm.migration.plan.step.variable.VariableStep} reads values and
 * removes variables (used with renaming only).
 */
public interface ReadStrategy extends DeleteStrategy {

  /**
   * Returns the {@link TypedValue} of the variable {@code variableName}.
   *
   * @param stepExecutionContext the context of this strategy's execution
   * @param variableName         the variable to read
   * @return the {@link TypedValue} or {@link Optional#empty()}
   */
  Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName);

}
