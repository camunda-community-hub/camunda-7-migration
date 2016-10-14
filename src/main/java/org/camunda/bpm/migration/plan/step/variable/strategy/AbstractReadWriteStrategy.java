package org.camunda.bpm.migration.plan.step.variable.strategy;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import java.util.Optional;

public class AbstractReadWriteStrategy {

	protected RuntimeService getRuntimeService(StepExecutionContext stepExecutionContext) {
		return stepExecutionContext.getProcessEngine().getRuntimeService();
	}

	protected TaskService getTaskService(StepExecutionContext stepExecutionContext) {
		return stepExecutionContext.getProcessEngine().getTaskService();
	}

	protected Optional<String> getTaskId(StepExecutionContext context, String taskDefinitionKey) {
		Optional<Task> task = Optional.ofNullable(context.getProcessEngine().getTaskService().createTaskQuery()
				.processInstanceId(context.getProcessInstanceId())
				.taskDefinitionKey(taskDefinitionKey)
				.singleResult());
		return task.map(Task::getId);
	}

}
