package org.camunda.bpm.migration.examples;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.migration.plan.DeploymentSpec;
import org.camunda.bpm.migration.plan.MigrationPlan;
import org.camunda.bpm.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.migration.plan.step.MappingStep;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Snippets {

	public void createMigrationPlan() {
		ProcessDefinitionSpec source = null;
		ProcessDefinitionSpec destination = null;

		MigrationPlan.builder().from(source).to(destination)
				.build();
	}

	public void specByDefinitionKeyAndVersionTag() {
		ProcessDefinitionSpec byDefinitionKeyAndVersionTag = ProcessDefinitionSpec.builder()
				.processDefinitionKey("myProcess")
				.versionTag("1")
				.build();

	}

	public void specByDefinitionKeyAndDeploymentDate() {
		DeploymentSpec year2015 = DeploymentSpec.builder()
				.earliestDeployment(ZonedDateTime.of(2015,01,01,0,0,0,0, ZoneId.of("CET")))
				.latestDeployment(ZonedDateTime.of(2015,12,31,23,59,59,0, ZoneId.of("CET")))
				.build();

		ProcessDefinitionSpec byDefinitionKeyAndDeploymentDate = ProcessDefinitionSpec.builder()
				.processDefinitionKey("myProcess")
				.deploymentSpec(year2015)
				.build();
	}

	public void createMappingStep() {
		RuntimeService runtimeService = null;

		//The MigrationPlan checks the existence of the source and target ProcessDefinitions
		BiFunction<String, String, org.camunda.bpm.engine.migration.MigrationPlan> camundaMigrationPlan =
				(source, target) -> runtimeService
				.createMigrationPlan(source, target)
				.mapEqualActivities()
				.build();
		MappingStep mappingStep = new MappingStep(camundaMigrationPlan);
	}
}
