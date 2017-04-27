package org.camunda.bpm.extension.migration.plan.step.variable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.extension.migration.plan.step.variable.strategy.WriteStrategy;
import org.camunda.bpm.extension.migration.plan.step.variable.strategy.ReadStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VariableStepTest {

  private static final String VARIABLE = "VARIABLE";
  private static final String TARGET = "TARGET";

  @Mock
  private StepExecutionContext stepExecutionContext;

  @Mock
  private ReadStrategy readStrategy;

  @Mock
  private WriteStrategy writeStrategy;

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
  public void writes_null_values() {
    TypedValue originalValue = prepareOriginalValue(Variables.longValue(null));

    variableStep.perform(stepExecutionContext);

    verify(writeStrategy).write(stepExecutionContext, VARIABLE, originalValue);
  }

  @Test
  public void uses_provided_conversion() {
    //Given a variable with originalValue
    TypedValue originalValue = prepareOriginalValue();
    //And a conversion function that converts originalValue to convertedValue
    TypedValue convertedValue = Variables.booleanValue(Boolean.TRUE);
    @SuppressWarnings("unchecked")
    Conversion conversion = mock(Conversion.class);
    when(conversion.apply(originalValue)).thenReturn(convertedValue);
    //And a variableStep with that conversion function
    variableStep.setConversion(conversion);

    //When performing the variable step
    variableStep.perform(stepExecutionContext);

    //then the conversion function is called with the originalValue
    verify(conversion).apply(originalValue);
    //and the writerStrategy is called with the convertedValue
    verify(writeStrategy).write(stepExecutionContext, VARIABLE, convertedValue);
  }

  @Test
  public void renames_variable() {
    //Given a variable VARIABLE
    TypedValue originalValue = prepareOriginalValue();
    //and a variableStep that renames VARIABLE to TARGET
    variableStep.setTargetVariableName(TARGET);

    //when performing the variableStep
    variableStep.perform(stepExecutionContext);

    //then variable TARGET is written
    verify(writeStrategy).write(stepExecutionContext, TARGET, originalValue);
    //and variable VARIABLE is deleted
    verify(readStrategy).remove(stepExecutionContext, VARIABLE);
  }

  @Before
  public void setUp() throws Exception {
    variableStep = VariableStep.builder()
      .readStrategy(readStrategy)
      .writeStrategy(writeStrategy)
      .sourceVariableName(VARIABLE)
      .build();
    when(readStrategy.read(any(), anyString())).thenReturn(Optional.empty());
  }

  private TypedValue prepareOriginalValue() {
    return prepareOriginalValue(Variables.stringValue("VALUE"));
  }

  private TypedValue prepareOriginalValue(TypedValue typedValue) {
    when(readStrategy.read(any(), anyString())).thenReturn(Optional.of(typedValue));
    variableStep.prepare(stepExecutionContext);
    return typedValue;
  }

}
