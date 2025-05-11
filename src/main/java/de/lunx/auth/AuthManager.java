package de.lunx.auth;

import com.google.gson.*;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class AuthManager {

    private final File authFile;
    private final Map<String, User> users = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AuthManager(File file) {
        this.authFile = file;
        load();
    }

    public boolean register(String username, String password, String role) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, hash(password), role));
        save();
        return true;
    }

    public Optional<User> authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.hashedPassword.equals(hash(password))) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Collection<User> listUsers() {
        return users.values();
    }

    private void load() {
        if (!authFile.exists()) {
            authFile.getParentFile().mkdirs();
            save(); return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(authFile), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            root.entrySet().forEach(entry -> {
                JsonObject userObj = entry.getValue().getAsJsonObject();
                users.put(entry.getKey(), new User(
                        entry.getKey(),
                        userObj.get("password").getAsString(),
                        userObj.get("role").getAsString()
                ));
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load auth file", e);
        }
    }

    private void save() {
        JsonObject root = new JsonObject();
        for (var entry : users.entrySet()) {
            JsonObject user = new JsonObject();
            user.addProperty("password", entry.getValue().hashedPassword);
            user.addProperty("role", entry.getValue().role);
            root.add(entry.getKey(), user);
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(authFile), StandardCharsets.UTF_8)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save auth file", e);
        }
    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public record User(String username, String hashedPassword, String role) {}
}
