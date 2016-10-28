package org.camunda.bpm.migration.plan.step.variable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.migration.plan.step.Step;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.migration.plan.step.variable.strategy.ReadStrategy;

/**
 * Deletes a variable.
 */
@RequiredArgsConstructor
public class VariableDeleteStep implements Step {

	@NonNull
	private ReadStrategy readStrategy;

	@Setter @Getter @NonNull
	private String variableName;

	@Override
	public void perform(StepExecutionContext context) {
		readStrategy.remove(context, variableName);
	}
}
