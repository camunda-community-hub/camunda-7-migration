package org.camunda.bpm.migration.plan.step.model;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.migration.MigrationPlanExecutionBuilder;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ModelStepTest {

  private static final String SOURCE = "SOURCE";
  private static final String TARGET = "TARGET";

  @Mock
  private MigrationPlan migrationPlan;

  @Spy
  private MigrationPlanFactory planFactory = new MigrationPlanFactory();

  @Mock
  private StepExecutionContext stepExecutionContext;

  @Mock
  private RuntimeService runtimeService;

  @Mock
  private MigrationPlanExecutionBuilder builder;

  private ModelStep mappingStep;

  @Test
  public void create_migrationPlan() throws Exception {

    mappingStep.perform(stepExecutionContext);

    verify(planFactory).apply(SOURCE, TARGET);
  }

  @Test
  public void execute_migrationPlan() throws Exception {
    mappingStep.perform(stepExecutionContext);

    verify(runtimeService).newMigration(migrationPlan);
    verify(builder).execute();
  }

  @Before
  public void setUp() throws Exception {
    mappingStep = new ModelStep(planFactory);

    ProcessEngine processEngine = mock(ProcessEngine.class);

    when(stepExecutionContext.getProcessEngine()).thenReturn(processEngine);
    when(stepExecutionContext.getSourceProcessDefinitionId()).thenReturn(SOURCE);
    when(stepExecutionContext.getTargetProcessDefinitionId()).thenReturn(TARGET);
    when(processEngine.getRuntimeService()).thenReturn(runtimeService);
    when(runtimeService.newMigration(migrationPlan)).thenReturn(builder);
    when(builder.processInstanceIds(anyList())).thenReturn(builder);
  }

  private class MigrationPlanFactory implements org.camunda.bpm.migration.plan.step.model.MigrationPlanFactory {
    @Override
    public MigrationPlan apply(String s, String s2) {
      return migrationPlan;
    }
  }
}
