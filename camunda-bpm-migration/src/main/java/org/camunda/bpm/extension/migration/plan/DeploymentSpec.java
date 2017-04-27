package org.camunda.bpm.extension.migration.plan;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

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
