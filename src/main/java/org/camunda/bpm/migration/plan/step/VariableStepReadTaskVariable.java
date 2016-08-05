package org.camunda.bpm.migration.plan.step;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Optional;

@RequiredArgsConstructor
public class VariableStepReadTaskVariable extends AbstractVariableStepStrategy implements VariableStepReadStrategy {

	@NonNull
	private final String taskDefinitionKey;

	@Override
	public Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName) {
		Optional<String> taskExecutionId = getTaskExecutionId(stepExecutionContext, taskDefinitionKey);
		return taskExecutionId.map(
				id -> getRuntimeService(stepExecutionContext).getVariableLocalTyped(id, variableName)
		);
	}

	@Override
	public void remove(StepExecutionContext stepExecutionContext, String variableName) {
		Optional<String> taskExecutionId = getTaskExecutionId(stepExecutionContext, taskDefinitionKey);
		taskExecutionId.ifPresent(
				id -> getRuntimeService(stepExecutionContext).removeVariable(id, variableName)
		);
	}
}
