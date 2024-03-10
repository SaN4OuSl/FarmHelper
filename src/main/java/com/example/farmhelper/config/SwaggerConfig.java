package com.example.farmhelper.config;

import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.LoginEndpointBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;


@Configuration
@EnableWebMvc
public class SwaggerConfig {

    private static final String APP_NAME = "swagger-ui";
    private static final String TOKEN_NAME = "access_token";
    private static final String OAUTH_NAME = "oauth2";
    private final String clientId;
    private final String realm;
    private final String keycloakAuthServerUrl;

    public SwaggerConfig(KeycloakConfigProperties keycloakConfigProperties) {
        clientId = keycloakConfigProperties.getResource();
        realm = keycloakConfigProperties.getRealm();
        keycloakAuthServerUrl = keycloakConfigProperties.getAuthServerUrl();
    }

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return SecurityConfigurationBuilder.builder().clientId(clientId).realm(realm)
            .appName(APP_NAME).build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
            .paths(PathSelectors.any())
            .build().apiInfo(apiInfo()).securitySchemes(
                (List<SecurityScheme>) buildSecurityScheme())
            .securityContexts(buildSecurityContext());
    }

    private List<SecurityContext> buildSecurityContext() {
        List<SecurityReference> securityReferences = Collections.singletonList(
            SecurityReference.builder().reference(OAUTH_NAME).scopes(new AuthorizationScope[] {})
                .build());

        SecurityContext context =
            SecurityContext.builder().forPaths(s -> true).securityReferences(securityReferences)
                .build();

        return Collections.singletonList(context);
    }

    private List<? extends SecurityScheme> buildSecurityScheme() {
        LoginEndpoint login = new LoginEndpointBuilder().url(
            keycloakAuthServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth").build();
        List<GrantType> grantTypes =
            Collections.singletonList(new ImplicitGrant(login, TOKEN_NAME));

        return Collections.<SecurityScheme>singletonList(
            new OAuth(OAUTH_NAME, Collections.emptyList(), grantTypes));
    }

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Techradar API").build();
    }
}