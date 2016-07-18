package org.camunda.bpm.migration.plan.step;

import lombok.Builder;
import lombok.Data;
import org.camunda.bpm.engine.ProcessEngine;

import java.util.List;

@Data
@Builder
public class StepExecutionContext {
	private ProcessEngine processEngine;
	private String sourceProcessDefinitionId;
	private String targetProcessDefinitionId;
	private List<String> processInstanceIds;
}
