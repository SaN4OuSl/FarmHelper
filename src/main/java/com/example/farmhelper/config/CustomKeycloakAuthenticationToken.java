package com.example.farmhelper.config;

import java.util.Collection;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomKeycloakAuthenticationToken
    extends KeycloakAuthenticationToken {
    private final UserPrincipal userPrincipal;

    public CustomKeycloakAuthenticationToken(KeycloakAccount account, boolean interactive,
                                             Collection<? extends GrantedAuthority> authorities,
                                             UserPrincipal userPrincipal) {
        super(account, interactive, authorities);
        this.userPrincipal = userPrincipal;
    }

    @Override
    public Object getPrincipal() {
        return this.userPrincipal;
    }

}
