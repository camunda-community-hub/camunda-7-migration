package org.camunda.bpm.extension.migration.plan.step.variable;

import static org.mockito.Mockito.verify;

import org.camunda.bpm.extension.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.extension.migration.plan.step.variable.strategy.DeleteStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VariableDeleteStepTest {

  public static final String VARIABLE_NAME = "FOO";

  @Mock
  private DeleteStrategy deleteStrategy;

  @Mock
  private StepExecutionContext stepExecutionContext;

  @Test
  public void deletes_using_strategy() {
    //Given a VariableDeleteStep configured with a certain strategy and variable name
    VariableDeleteStep variableDeleteStep = VariableDeleteStep.builder()
      .deleteStrategy(deleteStrategy)
      .variableName(VARIABLE_NAME).build();

    //When performing the step
    variableDeleteStep.perform(stepExecutionContext);

    //Then the strategy is used to actually remove the variable.
    verify(deleteStrategy).remove(stepExecutionContext, VARIABLE_NAME);

  }

}
