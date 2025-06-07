package com.learning_platform.utils;

import jakarta.annotation.PostConstruct;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PasswordHashManager {

  private StandardPBEStringEncryptor standardPBEStringEncryptor;

  @Value("${encryption-key:defaultEncryptionKey123}")
  private String encryptionKey;

  @PostConstruct
  private void init() {
    this.standardPBEStringEncryptor = new StandardPBEStringEncryptor();
    this.standardPBEStringEncryptor.setPassword(encryptionKey);
  }

  public String encrypt(String password) {
    return this.standardPBEStringEncryptor.encrypt(password);
  }

  public String decrypt(String passwordHash) {
    return this.standardPBEStringEncryptor.decrypt(passwordHash);
  }
}
