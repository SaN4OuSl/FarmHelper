package com.example.farmhelper.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(unique = true)
    @Getter
    @Setter
    private String username;

    @Column
    @Getter
    @Setter
    private String firstName;

    @Column
    @Getter
    @Setter
    private String lastName;

    @Column(unique = true)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String role;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    @Getter
    @Setter
    private Boolean isActive;

    @OneToMany(mappedBy = "user")
    @Getter
    @Setter
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    @Builder
    public User(String username, String firstName, String lastName, String email,
                String role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.isActive = true;
    }

}
