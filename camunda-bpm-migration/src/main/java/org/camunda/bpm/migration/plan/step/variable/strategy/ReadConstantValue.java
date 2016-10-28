package org.camunda.bpm.migration.plan.step.variable.strategy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import java.util.Optional;

@RequiredArgsConstructor
public class ReadConstantValue implements ReadStrategy {

	@NonNull
	private TypedValue value;

	@Override
	public Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName) {
		return Optional.of(value);
	}

	@Override
	public void remove(StepExecutionContext stepExecutionContext, String variableName) {
		//No-op
	}
}
