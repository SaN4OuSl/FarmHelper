package com.example.farmhelper.config;

import com.example.farmhelper.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserPrincipal {
    private Long id;
    private String username;
    private String role;

    public UserPrincipal() {
    }

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }

    public UserPrincipal(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
