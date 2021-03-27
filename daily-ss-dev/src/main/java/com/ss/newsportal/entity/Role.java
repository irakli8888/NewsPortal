package com.ss.newsportal.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;


    @Override
    public String getAuthority() {
        return name;
    }

    public static class Names {
        public static final String USER = "ROLE_USER";
        public static final String AUTHOR = "ROLE_AUTHOR";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
