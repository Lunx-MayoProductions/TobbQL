package de.lunx.http.restserver;

import com.google.gson.Gson;

public record QueryError(String title, String description) {
    public static String error(String title, String description) {
        return new Gson().toJson(new QueryError(title, description));
    }
}