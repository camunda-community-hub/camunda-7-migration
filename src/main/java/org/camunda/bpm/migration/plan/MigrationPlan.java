package org.camunda.bpm.migration.plan;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import org.camunda.bpm.migration.plan.step.Step;

import java.util.List;

@Data
@Builder
public class MigrationPlan {

	@NonNull
	private ProcessDefinitionSpec from;

	@NonNull
	private ProcessDefinitionSpec to;

	@NonNull
	@Singular
	private List<Step> steps;
}
