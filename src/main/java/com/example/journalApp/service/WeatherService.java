package com.example.journalApp.service;

import com.example.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {
    private static final String apiKey = "a9365e5641eed7a34457974b4c47546f";

    private static final String API = "http://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getWeather(String city){
        String finalAPI = API.replace("CITY", city).replace("API_KEY", apiKey);
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
        return response.getBody();
    }
}
