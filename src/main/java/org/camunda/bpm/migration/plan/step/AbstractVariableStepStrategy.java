package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.task.Task;

import java.util.Optional;

public class AbstractVariableStepStrategy {

	protected RuntimeService getRuntimeService(StepExecutionContext stepExecutionContext) {
		return stepExecutionContext.getProcessEngine().getRuntimeService();
	}

	protected Optional<String> getTaskExecutionId(StepExecutionContext context, String taskDefinitionKey) {
		Optional<Task> task = Optional.ofNullable(context.getProcessEngine().getTaskService().createTaskQuery()
				.processInstanceId(context.getProcessInstanceId())
				.taskDefinitionKey(taskDefinitionKey)
				.singleResult());
		return task.map(Task::getId);
	}

}
