package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

/**
 * A WriteStrategy defines how a {@link org.camunda.bpm.migration.plan.step.variable.VariableStep} writes variables.
 */
public interface WriteStrategy {

	/**
	 * Writes a variable named {@code variableName} with value {@code value}.
	 *
	 * @param stepExecutionContext the context of this strategy's execution
	 * @param variableName the variable to write
	 * @param value TypedValue to write, must not be null, but the TypedValue's value may.
	 */
	void write(StepExecutionContext stepExecutionContext, String variableName, TypedValue value);
}
