package de.lunx.auth;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.lunx.Main;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
public class AuthManager {

    private final File authFile;
    private final File roleFile;

    private List<User> users = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AuthManager(File authFile, File roleFile) {
        this.authFile = authFile;
        this.roleFile = roleFile;
        load();
    }

    public static AuthManager getInstance() {
        return Main.getInstance().getAuthManager();
    }

    public User register(String username, String password) {
        if (users.contains(getUser(username))) return null;
        User newUser = new User(username, password);
        save();
        return newUser;
    }

    public User register(User user) {
        users.add(user);
        return user;
    }

    public Optional<User> authenticate(String username, String password) {
        User user = getUser(username);
        if (user != null && user.getHashedPassword().equals(hash(password))) {
            return Optional.of(user);
        }
        return Optional.empty();
    }


    /**
     * Gets a {@link User} object by the username.
     * @param username The name the user is registered with
     * @return {@link User} object representing a user
     */
    @Nullable
    public User getUser(String username) {
        for (User u : users) if (u.getUsername().equals(username)) return u;
        return null;
    }

    @Nullable
    public Role getRole(String name) {
        for (Role r : roles) if (r.getName().equals(name)) return r;
        return null;
    }

    public Collection<User> listUsers() {
        return users;
    }

    public void load() {
        if (!authFile.getParentFile().mkdirs()) {
            save();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(authFile))) {
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            users = gson.fromJson(jsonStringBuilder.toString(),
                    new TypeToken<List<User>>(){}.getType());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(roleFile))) {
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            roles = gson.fromJson(jsonStringBuilder.toString(),
                    new TypeToken<List<Role>>(){}.getType());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(authFile)) {
            writer.write(gson.toJson(users));
        } catch (IOException e) {
            log.error("Failed to save auth file");
            log.error(e.getMessage());
        }
        try (FileWriter writer = new FileWriter(roleFile)) {
            writer.write(gson.toJson(roles));
        } catch (IOException e) {
            log.error("Failed to save role file");
            log.error(e.getMessage());
        }
    }

    public String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Failed to hash password");
            log.error(e.getMessage());
        }
        return "";
    }


}
