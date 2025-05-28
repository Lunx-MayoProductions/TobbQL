package de.lunx.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class User {
    private final UUID uniqueID;
    private final String username;
    private final String hashedPassword;
    private transient Role role;
    private UUID roleRaw;
    @Setter
    private boolean active;
    private final List<Permission> permissions = new ArrayList<>();

    public User(String username, String hashedPassword) {
        this.uniqueID = UUID.randomUUID();
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public User setRole(String role) {
        Role role1 = AuthManager.getInstance().getRole(role);
        if (role1 == null) return this;
        this.role = role1;
        roleRaw = role1.getUniqueID();
        return this;
    }

    public User addPermission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    public User addPermissions(Permission... permissions) {
        this.permissions.addAll(Arrays.stream(permissions).toList());
        return this;
    }

    public User addPermissions(List<Permission> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User user)) return false;
        return user.getUsername().equals(username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}