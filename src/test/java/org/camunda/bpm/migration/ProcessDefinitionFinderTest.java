package org.camunda.bpm.migration;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.migration.plan.DeploymentSpec;
import org.camunda.bpm.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.migration.test.DummyProcessDeployer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.migration.test.DummyProcessBuilder.build;

public class ProcessDefinitionFinderTest {

	private static final String KEY_A = ProcessDefinitionFinderTest.class.getSimpleName()+"A";
	private static final String KEY_B = ProcessDefinitionFinderTest.class.getSimpleName()+"B";
	private static final String TAG_V1 = "v1";
	private static final String TAG_V2 = "v2";
	private static final String SOURCE_1 = "source1";
	private static final String SOURCE_2 = "source2";

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule();

	private ProcessDefinitionFinder finder;

	private DummyProcessDeployer deployer;

	@Before
	public void deploy() {
		finder = new ProcessDefinitionFinder(processEngineRule.getRepositoryService());
		deployer = DummyProcessDeployer.builder().repositoryService(processEngineRule.getRepositoryService())
				.source(SOURCE_1)
				.model(build(KEY_A, TAG_V1))
				.model(build(KEY_B, TAG_V2))
				.build();
		deployer.deploy();
	}

	@After
	public void undeploy() {
		deployer.undeploy();
	}

	@Test
	public void find_by_processDefinitionKey() {
		ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().processDefinitionKey(KEY_B).build();
		Optional<ProcessDefinition> processDefinition = finder.find(spec);
		assertThat(processDefinition.isPresent()).isTrue();
	}

	@Test
	public void find_by_versionTag() {
		ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().versionTag(TAG_V1).build();
		Optional<ProcessDefinition> processDefinition = finder.find(spec);
		assertThat(processDefinition.isPresent()).isTrue();
	}

	@Test
	public void find_by_version() {
		runWithAnotherDeployment(() -> {
			ProcessDefinitionSpec spec = ProcessDefinitionSpec.builder().processDefinitionVersion(2).build();
			Optional<ProcessDefinition> processDefinition = finder.find(spec);
			assertThat(processDefinition.isPresent()).isTrue();
		});
	}

	@Test
	public void find_by_deploymentSpec_earliest_latest() {
		ZonedDateTime beforeDeployment = ZonedDateTime.now();

		runWithAnotherDeployment(() -> {
			DeploymentSpec deploymentSpec = DeploymentSpec.builder()
					.earliestDeployment(beforeDeployment)
					.latestDeployment(ZonedDateTime.now())
					.build();

			Optional<ProcessDefinition> processDefinition = finder.find(with(deploymentSpec));
			assertThat(processDefinition.isPresent()).isTrue();
		});
	}

	@Test
	public void find_by_deploymentSpec_source() {
		runWithAnotherDeployment(() -> {
			DeploymentSpec deploymentSpec = DeploymentSpec.builder()
					.source(SOURCE_2)
					.build();

			Optional<ProcessDefinition> processDefinition = finder.find(with(deploymentSpec));
			assertThat(processDefinition.isPresent()).isTrue();
		});
	}

	@Test
	public void empty_result_when_deploymentSpec_has_no_match() {
		runWithAnotherDeployment(() -> {
			DeploymentSpec deploymentSpec = DeploymentSpec.builder()
					.source("nonExistentSource")
					.build();

			Optional<ProcessDefinition> processDefinition = finder.find(with(deploymentSpec));
			assertThat(processDefinition.isPresent()).isFalse();
		});
	}

	private ProcessDefinitionSpec with(DeploymentSpec deploymentSpec) {
		return ProcessDefinitionSpec.builder().deploymentSpec(deploymentSpec).build();
	}

	private void runWithAnotherDeployment(Runnable testFunction) {
		DummyProcessDeployer deployer2 = DummyProcessDeployer.builder().repositoryService(processEngineRule.getRepositoryService())
				.source(SOURCE_2)
				.model(build(KEY_A, TAG_V2))
				.build();
		deployer2.deploy();

		try {
			testFunction.run();
		} finally {
			deployer2.undeploy();
		}
	}
}
