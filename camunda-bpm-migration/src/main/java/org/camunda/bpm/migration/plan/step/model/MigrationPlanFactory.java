package org.camunda.bpm.migration.plan.step.model;

import org.camunda.bpm.engine.migration.MigrationPlan;

import java.util.function.BiFunction;

public interface MigrationPlanFactory extends BiFunction<String, String, MigrationPlan> {
}
