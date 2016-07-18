package org.camunda.bpm.migration.plan.step;

public interface MigrationStep {

	void perform(StepExecutionContext context);
}
