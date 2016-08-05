package org.camunda.bpm.migration.plan.step;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class VariableStep implements MigrationStep {

	@NonNull
	private VariableStepReadStrategy readStrategy;

	@NonNull
	private VariableStepWriteStrategy writeStrategy;

	@Setter @Getter @NonNull
	private String sourceVariableName;

	@Setter @Getter
	private String targetVariableName;

	@Setter @Getter
	private Function<TypedValue, TypedValue> converter;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<TypedValue> originalTypedValue;

	@Override
	public void prepare(StepExecutionContext context) {
		originalTypedValue = readStrategy.read(context, sourceVariableName);
	}

	@Override
	public void perform(StepExecutionContext context) {
		//TODO write value even if it's null?
		originalTypedValue
				.map(converter())
				.ifPresent(
						value -> writeStrategy.write(context, targetVariableName(), value)
				);
		if(isRename())
			readStrategy.remove(context, sourceVariableName);
	}

	private Function<TypedValue, TypedValue> converter() {
		return converter != null ? converter : Function.identity();
	}

	private String targetVariableName() {
		return isRename() ? targetVariableName : sourceVariableName;
	}

	private boolean isRename() {
		return targetVariableName != null;
	}
}
