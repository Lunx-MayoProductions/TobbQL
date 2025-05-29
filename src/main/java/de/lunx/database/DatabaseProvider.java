package de.lunx.database;

public interface DatabaseProvider {
    void connect();
    void disconnect();
    void logRequest(String origin, String method, String path, String query, String body);
}
