package com.example.farmhelper.util;

import com.example.farmhelper.config.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class SecurityContextUtils {

    private SecurityContextUtils() {
    }

    public static Long authenticatedUserId() {
        try {
            UserPrincipal principal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            return principal.getId();
        } catch (ClassCastException e) {
            log.error(
                "Principal is not of type TechRadarUserPrincipal. Cannot get authenticatedUserId");
            return null;
        }
    }

    public static String authenticatedUserName() {
        try {
            UserPrincipal principal =
                (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            return principal.getUsername();
        } catch (ClassCastException e) {
            log.warn(
                "Principal is not of type TechRadarUserPrincipal.");
            AccessToken accessToken = getAccessToken();
            return accessToken.getPreferredUsername();
        }

    }

    public static UserPrincipal getPrincipal() {
        try {
            return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        } catch (ClassCastException e) {
            log.error(
                "Principal is not of type TechRadarUserPrincipal. Cannot get authenticatedUser");
            return new UserPrincipal();
        }
    }

    private static AccessToken getAccessToken() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        SimpleKeycloakAccount simpleKeycloakAccount = (SimpleKeycloakAccount) details;
        return simpleKeycloakAccount.getKeycloakSecurityContext().getToken();
    }
}
