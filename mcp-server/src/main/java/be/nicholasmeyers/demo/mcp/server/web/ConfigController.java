package be.nicholasmeyers.demo.mcp.server.web;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class ConfigController {

    @Value("${issuer-uri}")
    private String issuerUri;

    private final RestClient restClient = RestClient.create();

    @GetMapping("/.well-known/oauth-authorization-server")
    public ResponseEntity<@NonNull String> config() {
        String metadataUrl = issuerUri + "/.well-known/oauth-authorization-server";

        String response = restClient.get()
                .uri(metadataUrl)
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
