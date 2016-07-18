# User Guide

## Creating a Migration Plan

```java
//please note that we do not(!) use Camunda's MigrationPlan
import org.camunda.bpm.migration.plan.MigrationPlan;

//create the migration plan
//source is a ProcessDefinitionSpec that specifies a single process definition who's instance shall be migrated
//destination is a ProcessDefinitionSpec that specifies a single process definition where the instances should be migrated to.
//migrationStep is a MigrationStep instance
MigrationPlan.builder()
		.from(source).to(destination)
		.step(migrationStep)
		.build();
```

## Creating a Camunda Mapping Step

```java
//please note that this time we are(!) using Camunda's MigrationPlan
//The MigrationPlan.build() method checks the existence of the source and target ProcessDefinitions,
//that's why only a function for creation is provided and not the MigrationPlan itself
BiFunction<String, String, org.camunda.bpm.engine.migration.MigrationPlan> camundaMigrationPlan =
		(source, target) -> runtimeService
		.createMigrationPlan(source, target)
		.mapEqualActivities()
		.build();
MappingStep mappingStep = new MappingStep(camundaMigrationPlan);
```

## Specifying Process Definitions

Here are some examples on how to specify a process definition for use as source or destination in a `MigrationPlan`.

### By Definition Key and Version Tag
```java
ProcessDefinitionSpec byDefinitionKeyAndVersionTag = ProcessDefinitionSpec.builder()
		.processDefinitionKey("myProcess")
		.versionTag("1")
		.build();
```

### By Definition Key and Deployment Date
```java
DeploymentSpec year2015 = DeploymentSpec.builder()
		.earliestDeployment(ZonedDateTime.of(2015,01,01,0,0,0,0, ZoneId.of("CET")))
		.latestDeployment(ZonedDateTime.of(2015,12,31,23,59,59,0, ZoneId.of("CET")))
		.build();

ProcessDefinitionSpec byDefinitionKeyAndDeploymentDate = ProcessDefinitionSpec.builder()
		.processDefinitionKey("myProcess")
		.deploymentSpec(year2015)
		.build();
```java
