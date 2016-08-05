package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.variable.value.TypedValue;

public interface VariableStepWriteStrategy {
	void write(StepExecutionContext stepExecutionContext, String variableName, TypedValue value);
}
