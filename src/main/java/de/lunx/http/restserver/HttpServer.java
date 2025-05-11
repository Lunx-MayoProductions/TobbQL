package de.lunx.http.restserver;

import com.google.gson.JsonElement;
import de.lunx.auth.AuthManager;
import de.lunx.data.DataManager;
import de.lunx.querying.QueryParser;
import spark.Spark;

import java.io.File;

public class HttpServer {

    public static void startServer(DataManager dataManager, AuthManager authManager) {
        QueryParser parser = new QueryParser(dataManager);

        Spark.port(25544);

        Spark.post("/query", (req, res) -> {
            String user = req.queryParams("user");
            String pass = req.queryParams("pass");
            String query = req.queryParams("query");

            if (user == null || pass == null || query == null) {
                res.status(400); return "Missing user/pass/query";
            }

            return authManager.authenticate(user, pass)
                    .map(u -> {
                        try {
                            JsonElement result = parser.parse(query);
                            return result.toString();
                        } catch (Exception e) {
                            res.status(400);
                            return "Query error: " + e.getMessage();
                        }
                    })
                    .orElseGet(() -> {
                        res.status(403); return "Invalid credentials";
                    });
        });

        Spark.get("/query", (req, res) -> {
            return authManager.listUsers();
        });
    }
}

