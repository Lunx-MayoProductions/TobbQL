package de.lunx.auth;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class Role {
    private final UUID uniqueID;
    private final String name;


    private final List<Permission> permissions = new ArrayList<>();

    public Role(String name) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;
    }

    public Role addPermission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    public Role addPermissions(Permission... permissions) {
        this.permissions.addAll(Arrays.stream(permissions).toList());
        return this;
    }

    public Role addPermissions(List<Permission> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }
}