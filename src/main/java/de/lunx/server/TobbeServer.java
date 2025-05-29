package de.lunx.server;

import de.lunx.data.DataManager;

@Deprecated(forRemoval = true, since = "0.1.0")
public interface TobbeServer {
    void start();
    void stop();
    DataManager dataManager();
}
