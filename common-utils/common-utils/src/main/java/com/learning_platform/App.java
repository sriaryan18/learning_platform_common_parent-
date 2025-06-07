package com.learning_platform;

/** Hello world! */
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.learning_platform")
public class App {
  public static void main(String[] args) {
    System.out.println("Common Utils");
  }
}
