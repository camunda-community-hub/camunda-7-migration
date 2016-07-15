package org.camunda.bpm.migration.plan;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class DeploymentSpec {
	String id;
	String name;
	ZonedDateTime earliestDeployment;
	ZonedDateTime latestDeployment;
	String source;
	String tenantId;
}
