package org.camunda.bpm.migration;

import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.migration.plan.MigrationPlan;
import org.camunda.bpm.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.migration.plan.step.MigrationStep;
import org.camunda.bpm.migration.plan.step.StepExecutionContext;
import org.camunda.bpm.migration.test.DummyProcessDeployer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.camunda.bpm.migration.test.DummyProcessBuilder.build;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class MigratorTest {

	private static final String KEY = MigratorTest.class.getSimpleName();
	private static final String V2 = "2.0";
	private static final ProcessDefinitionSpec SPEC_V2 = ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag(V2).build();
	private static final String V1 = "1.0";
	private static final ProcessDefinitionSpec SPEC_V1 = ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag(V1).build();

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Migrator migrator;

	private MigrationPlan migrationPlan;

	private DummyProcessDeployer deployer1;
	private DummyProcessDeployer deployer2;

	@Mock
	private MigrationStep migrationStep;

	@Test
	public void exception_when_source_not_found() {
		thrown.expectMessage(allOf(containsString("Source"), containsString("not found")));

		migrationPlan.setFrom(ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag("nonExistentVersion").build());

		migrator.migrate(migrationPlan);
	}

	@Test
	public void exception_when_destination_not_found() {
		thrown.expectMessage(allOf(containsString("Destination"), containsString("not found")));

		migrationPlan.setTo(ProcessDefinitionSpec.builder().processDefinitionKey(KEY).versionTag("nonExistentVersion").build());

		migrator.migrate(migrationPlan);
	}

	@Test
	public void applies_migration_steps() {
		migrationPlan.setSteps(Collections.singletonList(migrationStep));
		migrator.migrate(migrationPlan);

		Mockito.verify(migrationStep).perform(any(StepExecutionContext.class));
	}

	@Before
	public void setup_stuff() {
		migrator = new Migrator(processEngineRule.getProcessEngine());

		deployer1 = deployDummyProcess(V1);
		deployer2 = deployDummyProcess(V2);

		migrationPlan = MigrationPlan.builder()
				.from(SPEC_V1)
				.to(SPEC_V2)
				.build();
	}

	private DummyProcessDeployer deployDummyProcess(String version) {
		DummyProcessDeployer deployer = DummyProcessDeployer.builder().repositoryService(processEngineRule.getRepositoryService())
				.source(getClass().getSimpleName())
				.model(build(KEY, version))
				.build();
		deployer.deploy();
		return deployer;
	}

	@After
	public void undeploy() {
		deployer1.undeploy();
		deployer2.undeploy();
	}

}
