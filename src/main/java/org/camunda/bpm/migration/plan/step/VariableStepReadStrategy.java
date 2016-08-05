package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Optional;

public interface VariableStepReadStrategy {
	Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName);
	void remove(StepExecutionContext stepExecutionContext, String variableName);
}
