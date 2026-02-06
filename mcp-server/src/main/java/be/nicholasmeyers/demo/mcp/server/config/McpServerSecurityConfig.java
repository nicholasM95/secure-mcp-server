package be.nicholasmeyers.demo.mcp.server.config;

import org.springaicommunity.mcp.security.server.config.McpServerOAuth2Configurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class McpServerSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(@Value("${issuer-uri}") String issuerUri, HttpSecurity http) {
        return http
                .authorizeHttpRequests(authorize -> {
                    authorize.anyRequest().authenticated();
                })
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .with(
                        McpServerOAuth2Configurer.mcpServerOAuth2(),
                        (mcpAuthorization) -> {
                            mcpAuthorization.authorizationServer(issuerUri);
                            mcpAuthorization.validateAudienceClaim(true);
                        }
                )
                .build();
    }
}
