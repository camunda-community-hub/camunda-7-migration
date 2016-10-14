package org.camunda.bpm.migration.plan.step.variable.strategy;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;

import java.util.Optional;

@Slf4j
public class ReadProcessVariable extends AbstractReadWriteStrategy implements ReadStrategy {

	@Override
	public Optional<TypedValue> read(StepExecutionContext stepExecutionContext, String variableName) {
		log.info("reading process variable {}", variableName);
		return Optional.ofNullable(
				getRuntimeService(stepExecutionContext)
					.getVariableLocalTyped(stepExecutionContext.getProcessInstanceId(), variableName));
	}

	@Override
	public void remove(StepExecutionContext stepExecutionContext, String variableName) {
		log.info("removing process variable {}", variableName);
		getRuntimeService(stepExecutionContext)
				.removeVariable(stepExecutionContext.getProcessInstanceId(), variableName);
	}

}
