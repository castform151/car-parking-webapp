package com.group.CarParking.Util;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

public class RestService {

  private final RestTemplate restTemplate;

  public RestService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public static void sendEmail(String email, String slotId) {
    final String uri = "http://localhost:3000?to=" + email + "&slotId=" + slotId;

    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(uri, String.class);

    System.out.println(result);
  }

  public String sendMail(String email) {
    String url = "http://localhost:3000?to=" + email;
    return this.restTemplate.getForObject(url, String.class);
  }
}