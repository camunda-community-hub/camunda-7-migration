package org.camunda.bpm.extension.migration.plan.step.model;

import java.util.function.BiFunction;

import org.camunda.bpm.engine.migration.MigrationPlan;

public interface MigrationPlanFactory extends BiFunction<String, String, MigrationPlan> {
}
