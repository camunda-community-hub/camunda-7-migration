package org.camunda.bpm.migration.plan.step.variable.strategy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ReadTaskVariable extends AbstractReadWriteStrategy implements ReadStrategy {

	@NonNull
	private final String taskDefinitionKey;

	@Override
	public Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName) {
		Optional<String> taskId = getTaskId(stepExecutionContext, taskDefinitionKey);
		return taskId.map(
				id -> {
					log.info("reading task variable {} from {} (ID:{})", variableName, taskDefinitionKey, id);
					return getTaskService(stepExecutionContext).getVariableLocalTyped(id, variableName);
				}
		);
	}

	@Override
	public void remove(StepExecutionContext stepExecutionContext, String variableName) {
		Optional<String> taskId = getTaskId(stepExecutionContext, taskDefinitionKey);
		taskId.ifPresent(
				id -> {
					log.info("removing task variable {} from {} (ID:{})", variableName, taskDefinitionKey, id);
					getTaskService(stepExecutionContext).removeVariable(id, variableName);
				}
		);
	}
}
