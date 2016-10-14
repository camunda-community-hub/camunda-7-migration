package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

public interface WriteStrategy {
	void write(StepExecutionContext stepExecutionContext, String variableName, TypedValue value);
}
