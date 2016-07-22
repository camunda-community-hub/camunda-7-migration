package org.camunda.bpm.migration.plan.step;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.migration.MigrationPlanExecutionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MappingStepTest {

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

	private MappingStep mappingStep;

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
		mappingStep = new MappingStep(planFactory);

		ProcessEngine processEngine = mock(ProcessEngine.class);

		when(stepExecutionContext.getProcessEngine()).thenReturn(processEngine);
		when(stepExecutionContext.getSourceProcessDefinitionId()).thenReturn(SOURCE);
		when(stepExecutionContext.getTargetProcessDefinitionId()).thenReturn(TARGET);
		when(processEngine.getRuntimeService()).thenReturn(runtimeService);
		when(runtimeService.newMigration(migrationPlan)).thenReturn(builder);
		when(builder.processInstanceIds(anyList())).thenReturn(builder);
	}

	private class MigrationPlanFactory implements BiFunction<String, String, MigrationPlan> {
		@Override
		public MigrationPlan apply(String s, String s2) {
			return migrationPlan;
		}
	}
}
