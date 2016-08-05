package org.camunda.bpm.migration.plan.step;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Optional;

@RequiredArgsConstructor
public class VariableStepWriteTaskVariable extends  AbstractVariableStepStrategy implements VariableStepWriteStrategy {

	@NonNull
	private final String taskDefinitionKey;

	@Override
	public void write(StepExecutionContext stepExecutionContext, String variableName, TypedValue value) {
		Optional<String> taskExecutionId = getTaskExecutionId(stepExecutionContext, taskDefinitionKey);
		taskExecutionId.ifPresent(
				id -> getRuntimeService(stepExecutionContext).setVariable(id, variableName, value)
		);
	}

}
