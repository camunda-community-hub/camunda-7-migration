package org.camunda.bpm.migration.plan.step;

public interface Step {

	/**
	 * Prepare this step before changes are applied.
	 * E.g. save (partial) state of process instance before migration.
     */
	default void prepare(StepExecutionContext context) {}

	/**
	 * Perform changes to the process instance.
	 */
	void perform(StepExecutionContext context);
}
