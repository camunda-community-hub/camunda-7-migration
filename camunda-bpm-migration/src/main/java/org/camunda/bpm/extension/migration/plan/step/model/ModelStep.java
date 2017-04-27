package org.camunda.bpm.extension.migration.plan.step.model;

import java.util.Collections;

import org.camunda.bpm.extension.migration.plan.step.Step;
import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Executes a Camunda built-in migration plan.
 */
@RequiredArgsConstructor
public class ModelStep implements Step {

  @NonNull
  private MigrationPlanFactory migrationPlanFactory;

  @Override
  public void perform(StepExecutionContext context) {
    context.getProcessEngine().getRuntimeService()
      .newMigration(migrationPlanFactory.apply(context.getSourceProcessDefinitionId(), context.getTargetProcessDefinitionId()))
      .processInstanceIds(Collections.singletonList(context.getProcessInstanceId()))
      .execute();
  }
}
