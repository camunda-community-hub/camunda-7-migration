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
		/// add more steps in desired order ...
		.build();
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
```

## Creating a Camunda Mapping Step

```java
//please note that this time we are(!) using Camunda's MigrationPlan
//The MigrationPlan.build() method checks the existence of the source and target ProcessDefinitions,
//that's why only a function for creation is provided and not the MigrationPlan itself
MigrationPlanFactory camundaMigrationPlan =
		(source, target) -> runtimeService
		.createMigrationPlan(source, target)
		.mapEqualActivities()
		.build(); //could fail on startup!
MappingStep mappingStep = new MappingStep(camundaMigrationPlan);
```

## Creating a Variable Step

Variables are automatically transferred 1:1 by Camunda's MigrationPlan.
 This step is used for changing names, values or types of variables and
 for moving variables from process level to task level and vice versa.

The Variable Step uses three parameters that define the actual behaviour:

- `sourceVariableName`: the name of the source variable. Equals the target name, unless the variable shall be renamed. (see below for renaming)
- `readStrategy`: defines where to read the variable from. Predefined strategies are
  - `ReadProcessVariable`
  - `ReadTaskVariable`
- `writeStrategy`: defines where to write the variable to. Predefined strategies are
  - `WriteProcessVariable`
  - `WriteTaskVariable`

```java
VariableStep variableStep = new VariableStep(readStrategy, writeStrategy, sourceVariableName);
```

### Renaming a Variable

For renaming a process variable, use any read and write strategy you like
and set the `VariableStep`'s `targetVariableName` property:

 ```java
VariableStep variableStep = new VariableStep(readStrategy, writeStrategy, sourceVariableName);
variableStep.setTargetVariableName(targetVariableName);
```

### Changing a Variable's Type or Value

Changing a variable type or value requires a conversion function to be provided
via the `VariableStep`'s `conversion` property:

 ```java
VariableStep variableStep = new VariableStep(readStrategy, writeStrategy, sourceVariableName);
variableStep.setConversion(conversion);
```

The conversion function itself is an instance of the the `Conversion`
interface which in turn is a `UnaryOperator<TypedValue>`.
As the conversion works on Camunda's `TypedValue`s, one can easily change
the value and the type in one step.

A (much too verbose :wink: ) example for converting a "123-456-789" formatted invoice number string into a 123456789 long.
```java
Conversion conversionFunction = (TypedValue originalTypedValue) -> {
	String invoiceNumberWithoutDelimiters = originalTypedValue.getValue().toString().replace("-", "");
	return Variables.longValue(Long.valueOf(invoiceNumberWithoutDelimiters));
};
```

N.B.: It is guaranteed that the `TypedValue` argument is not null. The value's value may be null, though.

### Creating a New Variable

Nothing simpler than this! :)

Create a `ReadConstantValue` that returns a `TypedValue` of your choice. No further settings are required.

```java
ReadStrategy constantValue = new ReadConstantValue(Variables.integerValue(42));
WriteStrategy writeStrategy = new WriteProcessVariable();

VariableStep variableStep = new VariableStep(constantValue, writeStrategy, "theAnswer");
```

### Removing a Variable

Use a `VariableDeleteStep` with an appropriate `ReadStrategy`.
Set the step's `variableName` property to the name of the variable that shall be deleted.

```java
VariableDeleteStep variableDeleteStep = new VariableDeleteStep(readStrategy, variableName);
```
