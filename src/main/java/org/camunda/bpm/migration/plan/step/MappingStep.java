package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.migration.MigrationPlan;

import java.util.function.BiFunction;

/**
 * Executes a Camunda built-in migration plan.
 */
public class MappingStep implements MigrationStep {

	private BiFunction<String, String, MigrationPlan> migrationPlanFactory;

	public MappingStep(BiFunction<String, String, MigrationPlan> camundaMigrationPlan) {
		this.migrationPlanFactory = camundaMigrationPlan;
	}

	@Override
	public void perform(StepExecutionContext context) {
		context.getProcessEngine().getRuntimeService()
				.newMigration(migrationPlanFactory.apply(context.getSourceProcessDefinitionId(), context.getTargetProcessDefinitionId()))
				.processInstanceIds(context.getProcessInstanceIds())
				.execute();
	}
}
