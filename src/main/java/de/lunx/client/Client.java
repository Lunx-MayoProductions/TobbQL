package de.lunx.client;

public interface Client {
    public void execute(String tql);
    public void token(String token);
    public void customRequest(String body);
}
