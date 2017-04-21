package org.camunda.bpm.migration.plan;

import java.util.List;

import org.camunda.bpm.migration.plan.step.Step;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

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
