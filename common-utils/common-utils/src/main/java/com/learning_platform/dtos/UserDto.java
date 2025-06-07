package com.learning_platform.dtos;

import com.learning_platform.enums.PaymentType;
import com.learning_platform.enums.Role;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
  private String id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private PaymentType paymentType;
  private Role role;
  private LocalDateTime createdAt;
  private String organizationId;
  private String organizationName;
}
