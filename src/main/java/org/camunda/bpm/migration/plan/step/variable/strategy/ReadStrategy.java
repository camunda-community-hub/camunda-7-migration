package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import java.util.Optional;

//TODO either ReadStrategy is the wrong name or the remove method has to be moved somewhere else
public interface ReadStrategy {
	Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName);
	void remove(StepExecutionContext stepExecutionContext, String variableName);
}
