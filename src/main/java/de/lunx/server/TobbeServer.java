package de.lunx.server;

import de.lunx.data.DataManager;

public interface TobbeServer {
    void start();
    void stop();
    DataManager dataManager();
}
