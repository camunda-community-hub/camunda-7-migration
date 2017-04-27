package org.camunda.bpm.extension.migration;

import java.util.List;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.migration.plan.MigrationPlan;
import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    List<ProcessInstance> processInstances = processEngine.getRuntimeService().createProcessInstanceQuery()
      .processDefinitionId(sourceProcessDefinition.getId()).active().list();
    log.info("migrating {} process instance(s)", processInstances.size());
    processInstances
      .stream()
      .map(ProcessInstance::getId)
      .peek(processInstanceId -> log.info("migrating process instance ID {}", processInstanceId))
      .map(processInstanceId -> StepExecutionContext.builder().processEngine(processEngine)
        .sourceProcessDefinitionId(sourceProcessDefinition.getId())
        .targetProcessDefinitionId(targetProcessDefinition.getId())
        .processInstanceId(processInstanceId)
        .build())
      .forEach(stepExecutionContext -> {
        plan.getSteps().forEach(step -> step.prepare(stepExecutionContext));
        plan.getSteps().forEach(step -> step.perform(stepExecutionContext));
      });
  }
}
