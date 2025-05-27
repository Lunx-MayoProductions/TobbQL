package de.lunx;

import de.lunx.auth.AuthManager;
import de.lunx.data.DataManager;
import de.lunx.http.restserver.HttpServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.logging.Logger;

@Slf4j
@Getter
public class Main {
    private DataManager manager;
    private HttpServer server;

    @Getter
    private static Main instance;


    public static void main(String[] args) {
        instance = new Main();
        instance.start();
    }

    public void start() {
        log.info("Starting TobbQL server...");
        server = new HttpServer();

        manager = DataManager.create(new File("data"));
        AuthManager auth = new AuthManager(new File("auth/users.json"));
        auth.register("guest", "123", "admin");

        log.info("Starting HTTP server...");
        server.startServer(manager, auth);
    }
}