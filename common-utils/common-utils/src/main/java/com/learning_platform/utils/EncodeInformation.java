package com.learning_platform.utils;

import java.util.Base64;

public class EncodeInformation {

  public static String encodeInformation(String information) {
    Base64.Encoder encoder = Base64.getEncoder();
    String encodedString = encoder.encodeToString(information.getBytes());
    return encodedString;
  }

  public static String decodeInformation(String encodedString) {
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] decodedBytes = decoder.decode(encodedString);
    return new String(decodedBytes);
  }
}
