package org.camunda.bpm.extension.migration.examples;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class StartProcessListener {

  @Autowired
  private RuntimeService runtimeService;

  @Value("${server.port:8080}")
  private String port;

  @GetMapping("/startProcess")
  public String start() {
    log.info("Starting process main-v1");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("main", "Nr. 123", Variables.putValue("FormField_19huq07", "Nr. 123"));
    return "<a href=\"http://localhost:" +
      port +
      "/app/cockpit/default/#/process-instance/"
      + processInstance.getId()
      + "?viewbox=%7B%22Definitions_1%22%3A%7B%22x%22%3A-248%2C%22y%22%3A-30%2C%22width%22%3A1342%2C%22height%22%3A430%7D%7D\">click</a>";
  }
}
