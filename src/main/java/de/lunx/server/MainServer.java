package de.lunx.server;

import de.lunx.data.DataManager;

@Deprecated(forRemoval = true, since = "0.1.0")
public class MainServer implements TobbeServer{
    @Override
    public void start() {
        System.out.println("Starting Tobbe Main Thread Server....");
    }

    @Override
    public void stop() {
        System.out.println("Stopping Tobbe Main Thread Server....");
    }

    @Override
    public DataManager dataManager() {
        return null;
    }
}
