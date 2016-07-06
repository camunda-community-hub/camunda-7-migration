package org.camunda.bpm.migration.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Rule;
import org.junit.Test;

public class DummyProcessBuilderTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();

    @Test
    public void build() {
        BpmnModelInstance modelInstance = DummyProcessBuilder.build("foo");

        assertThat(modelInstance).isNotNull();
    }

}