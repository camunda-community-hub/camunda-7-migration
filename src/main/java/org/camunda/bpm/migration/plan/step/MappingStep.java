package org.camunda.bpm.migration.plan.step;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.migration.MigrationPlan;

import java.util.Collections;
import java.util.function.BiFunction;

/**
 * Executes a Camunda built-in migration plan.
 */
@RequiredArgsConstructor
public class MappingStep implements MigrationStep {

	@NonNull
	private BiFunction<String, String, MigrationPlan> migrationPlanFactory;

	@Override
	public void perform(StepExecutionContext context) {
		context.getProcessEngine().getRuntimeService()
				.newMigration(migrationPlanFactory.apply(context.getSourceProcessDefinitionId(), context.getTargetProcessDefinitionId()))
				.processInstanceIds(Collections.singletonList(context.getProcessInstanceId()))
				.execute();
	}
}
