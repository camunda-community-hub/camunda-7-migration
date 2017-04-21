package org.camunda.bpm.migration.test;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;

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
    BpmnModelInstance modelInstance = build(key);

    Collection<Process> modelElementsByType = modelInstance.getModelElementsByType(Process.class);
    Process process = modelElementsByType.iterator().next();
    process.setAttributeValueNs("http://camunda.org/schema/1.0/bpmn", "versionTag", versionTag);

    return modelInstance;
  }
}
