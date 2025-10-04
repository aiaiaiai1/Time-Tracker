package org.project.timetracker.auth;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "uuser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    public User(String loginId, String password, String username) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
    }

    public User() {

    }
}
