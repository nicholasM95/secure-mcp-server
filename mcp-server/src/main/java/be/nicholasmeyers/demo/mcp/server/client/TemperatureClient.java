package be.nicholasmeyers.demo.mcp.server.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TemperatureClient {

    private final RestClient restClient;

    public TemperatureClient(@Qualifier("temperatureRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public String getTemperature() {
        return restClient.get()
                .uri("/temperature")
                .retrieve()
                .body(String.class);
    }
}
