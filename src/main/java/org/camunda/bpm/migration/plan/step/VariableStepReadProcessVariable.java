package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Optional;

public class VariableStepReadProcessVariable extends AbstractVariableStepStrategy implements VariableStepReadStrategy {

	@Override
	public Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName) {
		return Optional.ofNullable(
				getRuntimeService(stepExecutionContext)
					.getVariableLocalTyped(stepExecutionContext.getProcessInstanceId(), variableName));
	}

	@Override
	public void remove(StepExecutionContext stepExecutionContext, String variableName) {
		getRuntimeService(stepExecutionContext)
				.removeVariable(stepExecutionContext.getProcessInstanceId(), variableName);
	}

}
