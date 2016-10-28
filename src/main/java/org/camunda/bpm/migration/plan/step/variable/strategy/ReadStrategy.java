package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import java.util.Optional;

/**
 * A ReadStrategy defines how a {@link org.camunda.bpm.migration.plan.step.variable.VariableStep} reads values and
 * removes variables (used with renaming only).
 */
public interface ReadStrategy {

	/**
	 * Returns the {@link TypedValue} of the variable {@code variableName}.
	 *
	 * @param stepExecutionContext the context of this strategy's execution
	 * @param variableName the variable to read
	 * @return the {@link TypedValue} or {@link Optional#empty()}
	 */
	Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName);

	/**
	 * Removes a variable. This is used in the context of renaming variables.
	 *
	 * This method is placed in the <strong>read</strong> interface because it's the source variable that is to be deleted.
	 * {@link WriteStrategy}s might not know about the source variable and are therefore unable to delete it.
	 *
	 * @param stepExecutionContext the context of this strategy's execution
	 * @param variableName the variable to delete
	 */
	void remove(StepExecutionContext stepExecutionContext, String variableName);
}
