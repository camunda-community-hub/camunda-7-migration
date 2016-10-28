package org.camunda.bpm.migration.test;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.List;

@Builder
public class DummyProcessDeployer {

	@NonNull
	@Setter
	private RepositoryService repositoryService;

	@Setter
	@Singular
	private List<BpmnModelInstance> models;

	@Setter
	private String source;

	@Getter
	private Deployment deployment;

	public void deploy() {
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
				.source(source);
		models.forEach(model -> deploymentBuilder.addModelInstance(resourceId(model), model));
		deployment = deploymentBuilder.deploy();
	}

	public void undeploy() {
		repositoryService.deleteDeployment(deployment.getId());
	}

	private String resourceId(BpmnModelInstance model) {
		return model.getDefinitions().getId() + ".bpmn";
	}

}
