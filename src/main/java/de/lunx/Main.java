package de.lunx;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.lunx.auth.AuthManager;
import de.lunx.data.DataManager;
import de.lunx.http.restserver.HttpServer;

import java.io.File;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class Main {
    public static DataManager manager;
    public static void main(String[] args) {
        try {
            manager = DataManager.create(new File("data"));
            AuthManager auth = new AuthManager(new File("auth/users.json"));
            auth.register("guest", "123", "admin");
            HttpServer.startServer(manager, auth);
        } catch (Exception e){
            Logger.getGlobal().info("Error: (Line 21, Main.java): " + e.getMessage());
            System.exit(1);
        }
    }

}