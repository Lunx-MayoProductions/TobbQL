package de.lunx;

import de.lunx.auth.AuthManager;
import de.lunx.auth.JWTUtil;
import de.lunx.data.Configuration;
import de.lunx.data.DataManager;
import de.lunx.http.restserver.HttpServer;
import de.lunx.setup.Setup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

@Slf4j
@Getter
public class Main {
    private DataManager dataManager;
    private HttpServer server;
    private AuthManager authManager;

    private String dataSecret;

    private JWTUtil jwt = null;

    @Getter
    private static Main instance;


    public static void main(String[] args) {
        instance = new Main();
        try{
            instance.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void start() {
        log.info("Starting TobbQL server...");
        server = new HttpServer();

        dataManager = DataManager.create(new File("data"));
        dataManager.loadConfig();

        if(dataManager.getConfiguration() == null){
            dataManager.setConfiguration(new Configuration());
        }


        jwt = new JWTUtil(dataManager.getConfiguration().getJwtSecret());
        dataSecret = dataManager.getConfiguration().getDataSecret();
        if (dataSecret.isBlank()) dataSecret = jwt.generateSecret(40);

        authManager = new AuthManager(
                new File("auth", "users.json"),
                new File("auth", "roles.json")
        );
        log.info("Loading saved data...");
        authManager.load();

        log.info("Loading data...");
        dataManager.loadData();

        log.info("Starting Setup...");
        if(!Setup.setupCompleted()){
            try {
                Setup.setup(TerminalBuilder.terminal(), dataManager.getConfiguration());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Starting HTTP server...");

        server.startServer();

        log.info("TobbQL is up and running!");
    }

    public static void printStackTrace(Logger l, Exception e) {
        l.error(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            l.error(element.toString());
        }
    }

    public static void printStackTraceLevel(Logger l, System.Logger.Level level, Exception e) {
        switch (level) {
            case DEBUG -> {
                l.debug(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    l.debug(element.toString());
                }
            }
            case WARNING -> {
                l.warn(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    l.warn(element.toString());
                }
            }
            case ERROR -> {
                l.error(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    l.error(element.toString());
                }
            }
            case INFO -> {
                l.info(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    l.info(element.toString());
                }
            }
        }
    }
}