package com.example.farmhelper.config;

import lombok.Data;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakConfigProperties {

    private String grantType = "client_credentials";
    private String resource;
    private String realm;
    private String authServerUrl;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {

        return KeycloakBuilder.builder()
            .grantType(grantType)
            .clientId(resource)
            .clientSecret(clientSecret)
            .serverUrl(authServerUrl)
            .realm(realm)
            .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
            .build();
    }
}
