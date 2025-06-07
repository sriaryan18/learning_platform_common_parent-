package com.learning_platform.enums;

public enum Role {
  STUDENT("STUDENT"),
  TEACHER("TEACHER"),
  ADMIN("ADMIN");

  private final String role;

  Role(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }
}
