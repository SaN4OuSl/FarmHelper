package com.example.farmhelper.config;

import com.example.farmhelper.repository.UserRepository;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
@Slf4j
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
        "/v2/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/actuator/**",
        "/health",
        "/security/principal"
    };

    private static final String[] FARM_USERS = {
        UserRoles.STOREKEEPER.name(),
        UserRoles.ACCOUNTANT.name(),
        UserRoles.ADMIN.name()
    };

    private static final String[] ADMIN_PATHS = {
        "/users/**",
        "/fields/**"
    };

    private static final String[] STOREKEEPER_PATHS = {
        "/crops/**",
        "/harvests/**",
        "/transactions/**",
        "/fields/get-current-fields",
        "/sale-invoices/execute/**",
        "/sale-invoices",
        "/file/harvests/**",
        "/file/transactions"
    };

    private static final String[] ACCOUNTANT_PATHS = {
        "/sale-invoices/**",
        "/file/**"
    };

    @Autowired
    private UserRepository userRepository;


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider =
            keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }


    @Override
    protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        return new KeycloakAuthenticationProvider() {

            @Override
            public Authentication authenticate(Authentication authentication) throws
                AuthenticationException {
                KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
                AccessToken accessToken =
                    token.getAccount().getKeycloakSecurityContext().getToken();
                return createCustomToken(token, accessToken);
            }

            private UserPrincipal retrievePrincipal(AccessToken accessToken) {
                String username = accessToken.getPreferredUsername();
                return userRepository.findByUsernameAndIsActive(username, true)
                    .map(UserPrincipal::new)
                    .orElseGet(() -> new UserPrincipal(username, ""));
            }

            private Authentication createCustomToken(KeycloakAuthenticationToken token,
                                                     AccessToken accessToken) {
                UserPrincipal principal = retrievePrincipal(accessToken);
                KeycloakRole role = new KeycloakRole(principal.getRole());
                return new CustomKeycloakAuthenticationToken(
                    token.getAccount(),
                    token.isInteractive(),
                    new SimpleAuthorityMapper().mapAuthorities(Collections.singletonList(role)),
                    principal);
            }
        };
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, AUTH_WHITELIST).permitAll()
            .antMatchers(HttpMethod.GET, STOREKEEPER_PATHS).hasAnyRole(FARM_USERS)
            .antMatchers(STOREKEEPER_PATHS).hasRole(UserRoles.STOREKEEPER.name())
            .antMatchers(ACCOUNTANT_PATHS).hasRole(UserRoles.ACCOUNTANT.name())
            .antMatchers(ADMIN_PATHS).hasRole(UserRoles.ADMIN.name());
    }
}
