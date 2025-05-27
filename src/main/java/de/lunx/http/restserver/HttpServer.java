package de.lunx.http.restserver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.lunx.auth.AuthManager;
import de.lunx.data.DataManager;
import de.lunx.querying.QueryParser;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {

    private final Gson GSON = new Gson();

    public void startServer(DataManager dataManager, AuthManager authManager) {
        QueryParser parser = new QueryParser(dataManager);

        Javalin app = Javalin.create()
                .post("/query", ctx -> {
                    String user = ctx.queryParam("user");
                    String pass = ctx.queryParam("pass");
                    String query = ctx.queryParam("query");

                    if (user == null || pass == null || query == null) {
                        ctx.status(400);
                        ctx.result("Missing user/pass/query");
                    }

                    ctx.result(new Gson().toJson(authManager.authenticate(user, pass)
                            .map(u -> {
                                try {
                                    JsonElement result = parser.parse(query);
                                    return result.toString();
                                } catch (Exception e) {
                                    ctx.status(400);
                                    return "Query error: " + e.getMessage();
                                }
                            })
                            .orElseGet(() -> {
                                ctx.status(403);
                                return "Invalid credentials";
                            })));
                })
                .get("/query", context -> {
                    context.result(GSON.toJson(authManager.listUsers()));
                })
                .start(25544);
        log.info("Started HTTP server on port 25544");
    }
}