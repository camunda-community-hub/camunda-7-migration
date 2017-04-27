package org.camunda.bpm.extension.migration.examples;

import org.camunda.bpm.extension.migration.examples.processmigration.UpgradeMainFromV1ToV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class StartMigration {

  @Autowired
  private UpgradeMainFromV1ToV2 upgradeMainFromV1ToV2;

  @GetMapping("/startMigration")
  public String start() {
    upgradeMainFromV1ToV2.run();
    return "started";
  }
}
