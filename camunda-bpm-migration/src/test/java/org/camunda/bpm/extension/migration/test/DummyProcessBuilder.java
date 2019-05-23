package org.camunda.bpm.extension.migration.test;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;

public class DummyProcessBuilder {

  /**
   * @return start -> user -> end
   */
  public static BpmnModelInstance build(String key) {
    return Bpmn.createExecutableProcess(key).startEvent("start").userTask("user").endEvent("end").done();
  }

  /**
   * @return start -> user -> end
   */
  public static BpmnModelInstance build(String key, String versionTag) {
    ProcessBuilder processBuilder = Bpmn.createExecutableProcess(key);

    if (versionTag != null) {
     processBuilder.camundaVersionTag(versionTag);
    }

    return processBuilder.startEvent("start").userTask("user").endEvent("end").done();
  }
}
