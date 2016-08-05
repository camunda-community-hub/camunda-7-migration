package org.camunda.bpm.migration.plan.step;

public interface MigrationStep {

	/**
	 * Prepare this step before changes are applied.
     */
	default void prepare(StepExecutionContext context) {}

	/**
	 * Perform changes to the process instance.
	 */
	void perform(StepExecutionContext context);
}
