package be.nicholasmeyers.demo.mcp.server.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class TokenExchangeClient {

    private final RestClient restClient;

    public TokenExchangeClient(@Qualifier("tokenExchangeRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public TokenExchangeResponse tokenExchange(String subjectToken) {
        MultiValueMap<@NonNull String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        formData.add("client_id", "token-exchange");
        formData.add("client_secret", "e37af147-ebef-4723-be20-0ba92bc25b08");
        formData.add("subject_token", subjectToken);
        formData.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
        formData.add("requested_token_type", "urn:ietf:params:oauth:token-type:access_token");
        formData.add("requested_issuer", "demo-users");

        return restClient.post()
                .uri("/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(TokenExchangeResponse.class);
    }

    @RegisterReflectionForBinding(TokenExchangeResponse.class)
    public record TokenExchangeResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") int expiresIn,
            @JsonProperty("refresh_expires_in") int refreshExpiresIn,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("not-before-policy") int notBeforePolicy,
            @JsonProperty("session_state") String sessionState,
            @JsonProperty("scope") String scope,
            @JsonProperty("issued_token_type") String issuedTokenType,
            @JsonProperty("account-link-url") String accountLinkUrl
    ) {}
}
