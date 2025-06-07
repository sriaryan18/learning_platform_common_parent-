package com.learning_platform.utils;

import com.learning_platform.dtos.ServiceConfigDto;
import com.learning_platform.dtos.ServiceConfigDto.EndpointConfig;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceApiClient {

  private final RestTemplate restTemplate;

  public ServiceApiClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public <T> ResponseEntity<T> callEndpoint(
      ServiceConfigDto config,
      String endpointKey,
      Object requestBody,
      Map<String, ?> uriVariables,
      Class<T> responseType) {
    EndpointConfig endpointConfig = config.getEndpoints().get(endpointKey);
    if (endpointConfig == null) {
      throw new IllegalArgumentException("No endpoint config found for key: " + endpointKey);
    }

    String url = config.getBaseUrl() + endpointConfig.getEndPointPath();
    HttpMethod method = endpointConfig.getMethodType();

    HttpEntity<?> entity = (requestBody != null) ? new HttpEntity<>(requestBody) : HttpEntity.EMPTY;

    return restTemplate.exchange(url, method, entity, responseType, uriVariables);
  }
}
