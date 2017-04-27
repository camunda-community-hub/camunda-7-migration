package org.camunda.bpm.extension.migration.plan.step.variable.strategy;

import java.util.Optional;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.extension.migration.plan.step.variable.VariableStep;

/**
 * A ReadStrategy defines how a {@link VariableStep} reads values and
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
