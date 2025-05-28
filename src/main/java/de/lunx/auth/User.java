package de.lunx.auth;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User {
    private final UUID uniqueID;
    private final String username;
    private final String hashedPassword;
    private Role role;
    private final List<Permission> permissions = new ArrayList<>();

    public User(String username, String hashedPassword) {
        this.uniqueID = UUID.randomUUID();
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public User set(String role) {
        //TODO: Update role
        return this;
    }
}