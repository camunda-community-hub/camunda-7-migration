package org.camunda.bpm.migration;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.migration.plan.MigrationPlan;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import java.util.List;
import java.util.stream.Collectors;

public class Migrator {

	private ProcessEngine processEngine;

	public Migrator(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	public void migrate(MigrationPlan plan) {
		ProcessDefinitionFinder processDefinitionFinder = new ProcessDefinitionFinder(processEngine.getRepositoryService());
		ProcessDefinition sourceProcessDefinition = processDefinitionFinder
				.find(plan.getFrom())
				.orElseThrow(() -> new RuntimeException("Source process definition not found!"));
		ProcessDefinition targetProcessDefinition = processDefinitionFinder
				.find(plan.getTo())
				.orElseThrow(() -> new RuntimeException("Destination process definition not found!"));

		List<String> processInstanceIds = processEngine.getRuntimeService().createProcessInstanceQuery()
				.processDefinitionId(sourceProcessDefinition.getId()).active().list()
				.stream().map(ProcessInstance::getId).collect(Collectors.toList());

		StepExecutionContext stepExecutionContext = StepExecutionContext.builder().processEngine(processEngine)
				.sourceProcessDefinitionId(sourceProcessDefinition.getId())
				.targetProcessDefinitionId(targetProcessDefinition.getId())
				.processInstanceIds(processInstanceIds)
				.build();

		plan.getSteps().forEach(step -> step.perform(stepExecutionContext));
	}
}
