package be.nicholasmeyers.demo.mcp.server.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean
    public RestClient tokenExchangeRestClient(@Value("${issuer-uri}") String temperatureApiIssuerUri) {
        return RestClient.builder()
                .baseUrl(temperatureApiIssuerUri)
                .build();
    }

    @Bean
    public RestClient temperatureRestClient(@Value("${temperature.api}") String temperatureApi, TokenExchangeClient tokenExchangeClient) {
        return RestClient.builder()
                .baseUrl(temperatureApi)
                .requestInterceptor(authInterceptor(tokenExchangeClient))
                .build();
    }

    private ClientHttpRequestInterceptor authInterceptor(TokenExchangeClient tokenExchangeClient) {
        return (request, body, execution) -> {
            String currentToken = getCurrentAccessToken();
            log.info("Current token {}", currentToken);
            TokenExchangeClient.TokenExchangeResponse exchangedToken = tokenExchangeClient.tokenExchange(currentToken);
            log.info("Token exchange: {}", exchangedToken.accessToken());
            request.getHeaders().setBearerAuth(exchangedToken.accessToken());
            return execution.execute(request, body);
        };
    }

    private String getCurrentAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }

        throw new IllegalStateException("Invalid access token in security context");
    }
}
