package com.learning_platform.enums;

public enum PaymentType {
  SELF("SELF"),
  GROUP("GROUP"),
  FREE("FREE");

  private final String paymentType;

  PaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public String getPaymentType() {
    return paymentType;
  }
}
