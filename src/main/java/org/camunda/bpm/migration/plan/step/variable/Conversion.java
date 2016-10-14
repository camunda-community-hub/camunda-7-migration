package org.camunda.bpm.migration.plan.step.variable;

import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.function.UnaryOperator;

public interface Conversion extends UnaryOperator<TypedValue> {

	Conversion ID = value -> value;
}
