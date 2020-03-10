package com.alten.springboot.taskmanager.config;

import java.util.Collection;

import org.springframework.security.core.userdetails.User;

public class PrincipalUser extends User {

    private static final long serialVersionUID = -3531439484732724601L;

    private final int id;

    public PrincipalUser(String username, String password, boolean enabled, boolean accountNonExpired,
                         boolean credentialsNonExpired, boolean accountNonLocked, Collection authorities, int id) {

        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = id;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

}