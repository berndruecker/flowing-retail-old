package io.flowing.retail.commands;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.camunda.bpm.spring.boot.starter.SpringBootProcessApplication;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.flowing.retail.commands.channel.ChannelStartup;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaSender;

@SpringBootApplication
@EnableProcessApplication
public class ApplicationBpmnMonitor  {

  public static void main(String... args) {
    ChannelStartup.startup( //
        "BPMN_MONITOR", //
        new KafkaSender(), //
        new KafkaChannelConsumer("bpmn-monitor"), //
        new BpmnMonitorEventConsumer());

    SpringApplication.run(ApplicationBpmnMonitor.class, args);
    createDemoUser();
  }

  public static void createDemoUser() {
    ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();

    addUser(engine, "demo", "demo", "Demo", "Demo");
    if (addGroup(engine, Groups.CAMUNDA_ADMIN, "Camunda BPM Admin", "demo")) {
      // create ADMIN authorizations on all built-in resources
      for (Resource resource : Resources.values()) {
        if (engine.getAuthorizationService().createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(ANY).count() == 0) {
          AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
          userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
          userAdminAuth.setResource(resource);
          userAdminAuth.setResourceId(ANY);
          userAdminAuth.addPermission(ALL);
          engine.getAuthorizationService().saveAuthorization(userAdminAuth);
        }
      }
    }

  }

  public static boolean addUser(ProcessEngine engine, String userName, String password, String firstname, String lastname) {
    if (engine.getIdentityService().isReadOnly()) {
      return false;
    }
    if (engine.getIdentityService().createUserQuery().userId(userName).count() > 0) {
      return false;
    }
    User user = engine.getIdentityService().newUser(userName);
    user.setFirstName(firstname);
    user.setLastName(lastname);
    user.setPassword(password);
    user.setEmail("demo@camunda.org");
    engine.getIdentityService().saveUser(user);
    return true;
  }

  

  public static boolean addGroup(ProcessEngine engine, String groupId, String groupName, String... members) {
    if (engine.getIdentityService().isReadOnly()) {
      return false;
    }
    if (engine.getIdentityService().createGroupQuery().groupId(groupId).count() > 0) {
      addMembership(engine, groupId, members);
      return false;
    }

    Group salesGroup = engine.getIdentityService().newGroup(groupId);
    salesGroup.setName(groupName);
    salesGroup.setType("WORKFLOW");
    engine.getIdentityService().saveGroup(salesGroup);

    // authorize groups for tasklist only:
    Authorization auth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
    auth.setGroupId(groupId);
    auth.addPermission(ACCESS);
    auth.setResourceId("tasklist");
    auth.setResource(APPLICATION);
    engine.getAuthorizationService().saveAuthorization(auth);

    addMembership(engine, groupId, members);
    return true;
  }

  public static void addMembership(ProcessEngine engine, String groupId, String... userIds) {
    for (String userId : userIds) {
      engine.getIdentityService().deleteMembership(userId, groupId);
      try {
        engine.getIdentityService().createMembership(userId, groupId);
      } catch (Exception ex) {
        // memebership already there - ignore
      }
    }
  }

}
