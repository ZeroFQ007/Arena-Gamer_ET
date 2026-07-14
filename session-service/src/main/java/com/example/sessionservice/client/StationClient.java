package com.example.sessionservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@Slf4j
public class StationClient {

    private final RestClient restClient;

    public StationClient(RestClient.Builder builder,
                         @Value("${services.station-service.url:http://localhost:8083}") String stationServiceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restClient = builder
                .baseUrl(stationServiceUrl)
                .requestFactory(factory)
                .build();
    }

    public Optional<StationResponse> findById(Long stationId) {
        try {
            StationResponse response = restClient.get()
                    .uri("/api/stations/" + stationId)
                    .retrieve()
                    .body(StationResponse.class);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.error("Estación no encontrada con id: {}", stationId);
            return Optional.empty();
        }
    }

    public boolean existsStation(Long stationId) {
        return findById(stationId).isPresent();
    }
}