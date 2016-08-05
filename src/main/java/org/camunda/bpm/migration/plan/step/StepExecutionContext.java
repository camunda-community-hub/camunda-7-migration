package org.camunda.bpm.migration.plan.step;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.camunda.bpm.engine.ProcessEngine;

@ToString
@EqualsAndHashCode
@Builder
@Getter
public class StepExecutionContext {
	private ProcessEngine processEngine;
	private String sourceProcessDefinitionId;
	private String targetProcessDefinitionId;
	private String processInstanceId;
}
