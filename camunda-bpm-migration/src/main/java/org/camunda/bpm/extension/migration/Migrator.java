package org.camunda.bpm.extension.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.migration.plan.MigrationPlan;
import org.camunda.bpm.extension.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;

@Slf4j
public class Migrator {

  private final ProcessEngine processEngine;
  private final ProcessDefinitionFinder processDefinitionFinder;

  public Migrator(ProcessEngine processEngine) {
    this.processEngine = processEngine;
     this.processDefinitionFinder = new ProcessDefinitionFinder(processEngine.getRepositoryService());
  }

  public void migrate(MigrationPlan plan) {
    final ProcessDefinition targetProcessDefinition = targetDefinition(plan.getTo());


    final List<ProcessDefinition> sourceProcessDefinitions = sourceDefinitions(plan.getFrom());
    final List<ProcessInstance> processInstances = findSourceProcessInstances(sourceProcessDefinitions);
    log.info("migrating {} process instance(s)", processInstances.size());


    processInstances
      .stream()
      .peek(processInstance -> log.info("migrating process instance ID {}", processInstance))
      .map(processInstance -> StepExecutionContext.builder().processEngine(processEngine)
        .sourceProcessDefinitionId(processInstance.getProcessDefinitionId())
        .targetProcessDefinitionId(targetProcessDefinition.getId())
        .processInstanceId(processInstance.getProcessInstanceId())
        .build())
      .forEach(stepExecutionContext -> {
        plan.getSteps().forEach(step -> step.prepare(stepExecutionContext));
        plan.getSteps().forEach(step -> step.perform(stepExecutionContext));
      });
  }

  private List<ProcessInstance> findSourceProcessInstances(
    List<ProcessDefinition> sourceProcessDefinitions) {
    List<ProcessInstance> processInstances = new ArrayList<>();

    for (ProcessDefinition def : sourceProcessDefinitions) {
      processInstances.addAll(
        processEngine.getRuntimeService().createProcessInstanceQuery()
          .processDefinitionId(def.getId()).active().list()
      );
    }
    return processInstances;
  }

  private List<ProcessDefinition> sourceDefinitions(ProcessDefinitionSpec from) {
    return Optional.of(processDefinitionFinder.findAll(from))
      .filter(l -> !l.isEmpty())
      .orElseThrow(illegalState("Source process definition not found!"));
  }

  private ProcessDefinition targetDefinition(ProcessDefinitionSpec toSpec) {
    return processDefinitionFinder.findOne(toSpec)
        .orElseThrow(illegalState("Destination process definition not found!"));
  }

  private static Supplier<IllegalStateException> illegalState(String pattern, Object... args) {
    return () -> new IllegalStateException(String.format(pattern, args));
  }
}
