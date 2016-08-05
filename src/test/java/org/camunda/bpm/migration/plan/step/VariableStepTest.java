package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VariableStepTest {

	private static final String VARIABLE = "VARIABLE";
	private static final String TARGET = "TARGET";

	@Mock
	private StepExecutionContext stepExecutionContext;

	@Mock
	private VariableStepReadStrategy readStrategy;

	@Mock
	private VariableStepWriteStrategy writeStrategy;

	private VariableStep variableStep;

	@Test
	public void reads_using_strategy() {
		variableStep.prepare(stepExecutionContext);
		verify(readStrategy).read(stepExecutionContext, VARIABLE);
	}

	@Test
	public void writes_using_strategy() {
		TypedValue originalValue = prepareOriginalValue();

		variableStep.perform(stepExecutionContext);

		verify(writeStrategy).write(stepExecutionContext, VARIABLE, originalValue);
	}

	@Test
	public void converts_and_uses_value() {
		TypedValue originalValue = prepareOriginalValue();
		TypedValue convertedValue = Variables.booleanValue(Boolean.TRUE);
		@SuppressWarnings("unchecked")
		Function<TypedValue, TypedValue> converter = mock(Function.class);
		when(converter.apply(originalValue)).thenReturn(convertedValue);
		variableStep.setConverter(converter);

		variableStep.perform(stepExecutionContext);

		verify(converter).apply(originalValue);
		verify(writeStrategy).write(stepExecutionContext, VARIABLE, convertedValue);
	}

	@Test
	public void renames_variable() {
		TypedValue originalValue = prepareOriginalValue();
		variableStep.setTargetVariableName(TARGET);

		variableStep.perform(stepExecutionContext);

		verify(writeStrategy).write(stepExecutionContext, TARGET, originalValue);
		verify(readStrategy).remove(stepExecutionContext, VARIABLE);
	}

	@Before
	public void setUp() throws Exception {
		variableStep = new VariableStep(readStrategy, writeStrategy, VARIABLE);
		when(readStrategy.read(any(), anyString())).thenReturn(Optional.empty());
	}

	private TypedValue prepareOriginalValue() {
		TypedValue typedValue = Variables.stringValue("VALUE");
		when(readStrategy.read(any(),anyString())).thenReturn(Optional.of(typedValue));
		variableStep.prepare(stepExecutionContext);
		return typedValue;
	}

}
