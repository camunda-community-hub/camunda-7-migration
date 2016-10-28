package org.camunda.bpm.migration.examples;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.migration.examples.processmigration.UpgradeMainFromV1ToV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartProcessListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private UpgradeMainFromV1ToV2 upgradeMainFromV1ToV2;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("Starting process main-v1");
		runtimeService.startProcessInstanceByKey("main-v1", "Nr. 001", Variables.putValue("FormField_19huq07", "Nr. 001"));

		upgradeMainFromV1ToV2.run();
	}
}
