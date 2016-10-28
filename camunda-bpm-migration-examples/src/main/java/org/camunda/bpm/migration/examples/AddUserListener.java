package org.camunda.bpm.migration.examples;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;

@Component
@Slf4j
public class AddUserListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private AuthorizationService authorizationService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("Adding user foo:bar");

		User foo = identityService.newUser("foo");
		foo.setPassword("bar");
		foo.setFirstName("Foo");
		foo.setLastName("Bär");
		identityService.saveUser(foo);

		if(identityService.createGroupQuery().groupId(Groups.CAMUNDA_ADMIN).count() == 0) {
			log.info("Creating group camunda-admin ");
			Group camundaAdminGroup = identityService.newGroup(Groups.CAMUNDA_ADMIN);
			camundaAdminGroup.setName("camunda BPM Administrators");
			camundaAdminGroup.setType(Groups.GROUP_TYPE_SYSTEM);
			identityService.saveGroup(camundaAdminGroup);

			// create ADMIN authorizations on all built-in resources
			for (Resource resource : Resources.values()) {
				if(authorizationService.createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(ANY).count() == 0) {
					AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
					userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
					userAdminAuth.setResource(resource);
					userAdminAuth.setResourceId(ANY);
					userAdminAuth.addPermission(ALL);
					authorizationService.saveAuthorization(userAdminAuth);
				}
			}
		}

		identityService.createMembership("foo","camunda-admin");
	}

}
