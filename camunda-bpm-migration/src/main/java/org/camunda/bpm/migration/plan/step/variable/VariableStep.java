package org.camunda.bpm.migration.plan.step.variable;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.plan.step.Step;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.migration.plan.step.variable.strategy.ReadStrategy;
import org.camunda.bpm.migration.plan.step.variable.strategy.WriteStrategy;

import java.util.Optional;

/**
 * Advanced operations on variables.
 * <p>
 * Variables are already copied by the {@link org.camunda.bpm.migration.plan.step.model.ModelStep}, but advanced
 * operations like renaming, changing type or value are not supported.
 * <p>
 * This steps reads a variable from the old instance in its {@link #prepare(StepExecutionContext)} method.
 * Then applies a transformation and writes the result in its {@link #perform(StepExecutionContext)} method.
 * <p>
 * Source and target variable are determined by a {@link ReadStrategy} and a {@link WriteStrategy}.
 * The transformation is performed by a {@link Conversion}
 */
public class VariableStep implements Step {

	@NonNull
	private ReadStrategy readStrategy;

	@NonNull
	private WriteStrategy writeStrategy;

	@Setter @Getter @NonNull
	private String sourceVariableName;

	@Setter @Getter
	private String targetVariableName;

	@Setter @Getter
	private Conversion conversion;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<TypedValue> originalTypedValue;

	@Builder
	private VariableStep(ReadStrategy readStrategy, WriteStrategy writeStrategy, String sourceVariableName, String targetVariableName, Conversion conversion) {
		this.readStrategy = readStrategy;
		this.writeStrategy = writeStrategy;
		this.sourceVariableName = sourceVariableName;
		this.targetVariableName = targetVariableName;
		this.conversion = conversion;
	}

	@Override
	public void prepare(StepExecutionContext context) {
		originalTypedValue = readStrategy.read(context, sourceVariableName);
	}

	@Override
	public void perform(StepExecutionContext context) {
		originalTypedValue
				.map(converter())
				.ifPresent(
						value -> writeStrategy.write(context, targetVariableName(), value)
				);
		if(isRename())
			readStrategy.remove(context, sourceVariableName);
	}

	private Conversion converter() {
		return conversion != null ? conversion : Conversion.ID;
	}

	private String targetVariableName() {
		return isRename() ? targetVariableName : sourceVariableName;
	}

	private boolean isRename() {
		return targetVariableName != null;
	}
}
