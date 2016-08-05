package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.variable.value.TypedValue;

public class VariableStepWriteProcessVariable extends AbstractVariableStepStrategy implements VariableStepWriteStrategy {

	@Override
	public void write(StepExecutionContext stepExecutionContext, String variableName, TypedValue value) {
		getRuntimeService(stepExecutionContext)
				.setVariable(stepExecutionContext.getProcessInstanceId(), variableName, value);
	}
}
