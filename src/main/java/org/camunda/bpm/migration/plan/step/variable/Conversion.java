package org.camunda.bpm.migration.plan.step.variable;

import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.function.UnaryOperator;

/**
 * Defines how a variable's type and value are converted.
 */
public interface Conversion extends UnaryOperator<TypedValue> {

	/**
	 * Convenience shortcut to {@link UnaryOperator#identity()}
	 */
	Conversion ID = (Conversion) UnaryOperator.<TypedValue>identity();

}
