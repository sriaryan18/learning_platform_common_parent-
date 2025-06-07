package com.learning_platform.dtos;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
@Builder
public class ServiceConfigDto {

  private String baseUrl;
  private Map<String, EndpointConfig> endpoints;

  @Data
  public static class EndpointConfig {
    private String endPointPath;
    private HttpMethod methodType;

    public EndpointConfig(String endPointPath, HttpMethod methodType) {
      this.endPointPath = endPointPath;
      this.methodType = methodType;
    }

    public String getEndPointPath() {
      return this.endPointPath;
    }

    public HttpMethod getMethodType() {
      return this.methodType;
    }

    public void setEndPointPath(String endPointPath) {
      this.endPointPath = endPointPath;
    }

    public void setMethodType(HttpMethod methodType) {
      this.methodType = methodType;
    }
  }
}
