package io.flowing.retail.commands;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

import io.flowing.retail.adapter.FlowingStartup;

@SpringBootApplication
@EnableAutoConfiguration
@EnableProcessApplication
public class BpmnMonitorApplication  {

  public static void main(String... args) {
    FlowingStartup.startup("bpmn-monitor", new BpmnMonitorEventConsumer(), args);

    SpringApplication.run(BpmnMonitorApplication.class, args);

    // now install license and add default users to Camunda to be ready-to-go
    ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();    
    LicenseHelper.setLicense(engine);
    UserGenerator.createDefaultUsers(engine);
  }

}
