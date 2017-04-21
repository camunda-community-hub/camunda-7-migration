package org.camunda.bpm.migration.examples;

import org.camunda.bpm.spring.boot.starter.SpringBootProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExampleProcessApplication extends SpringBootProcessApplication {

  public static void main(String... args) {
    SpringApplication.run(ExampleProcessApplication.class, args);
  }

}
