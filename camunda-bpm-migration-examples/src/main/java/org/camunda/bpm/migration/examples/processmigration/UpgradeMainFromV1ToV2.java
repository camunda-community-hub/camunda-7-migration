package org.camunda.bpm.migration.examples.processmigration;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.camunda.bpm.migration.Migrator;
import org.camunda.bpm.migration.plan.MigrationPlan;
import org.camunda.bpm.migration.plan.ProcessDefinitionSpec;
import org.camunda.bpm.migration.plan.step.model.MigrationPlanFactory;
import org.camunda.bpm.migration.plan.step.model.ModelStep;
import org.camunda.bpm.migration.plan.step.variable.Conversion;
import org.camunda.bpm.migration.plan.step.variable.VariableStep;
import org.camunda.bpm.migration.plan.step.variable.strategy.ReadConstantValue;
import org.camunda.bpm.migration.plan.step.variable.strategy.ReadProcessVariable;
import org.camunda.bpm.migration.plan.step.variable.strategy.WriteProcessVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpgradeMainFromV1ToV2 {

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private RuntimeService runtimeService;

	private final MigrationPlanFactory camundaMigrationPlanFactory = (source, target) -> runtimeService
			.createMigrationPlan(source, target)
			.mapActivities("Task_AB", "Task_A")
			.build();

	private final ModelStep modelStep = new ModelStep(camundaMigrationPlanFactory);

	private final ProcessDefinitionSpec v1 = ProcessDefinitionSpec.builder()
			.versionTag("v1")
			.processDefinitionKey("main")
			.build();

	private final ProcessDefinitionSpec v2 = ProcessDefinitionSpec.builder()
			.versionTag("v2")
			.processDefinitionKey("main-redeploy")
			.build();

	private final Conversion convertInvoiceNumber = (TypedValue originalTypedValue) -> {
		String invoiceNumber = originalTypedValue.getValue().toString().substring(4);
		return Variables.longValue(Long.valueOf(invoiceNumber));
	};

	private final VariableStep rename_formField_19huq07_to_invoiceNumber = VariableStep.builder()
			.readStrategy(new ReadProcessVariable())
			.writeStrategy(new WriteProcessVariable())
			.sourceVariableName("FormField_19huq07")
			.targetVariableName("invoiceNumber")
			.conversion(convertInvoiceNumber)
			.build();

	private final VariableStep create_variable_numPieces = VariableStep.builder()
			.readStrategy(new ReadConstantValue(Variables.longValue(1L)))
			.writeStrategy(new WriteProcessVariable())
			.sourceVariableName("numPieces")
			.build();

	private final MigrationPlan migrationPlan = MigrationPlan.builder()
			.from(v1).to(v2)
			.step(modelStep)
			.step(rename_formField_19huq07_to_invoiceNumber)
			.step(create_variable_numPieces)
			.build();

	public void run() {
		new Migrator(processEngine).migrate(migrationPlan);
	}

}
