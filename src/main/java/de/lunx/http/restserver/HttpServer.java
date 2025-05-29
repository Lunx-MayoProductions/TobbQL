package de.lunx.http.restserver;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.lunx.Main;
import de.lunx.auth.AuthManager;
import de.lunx.auth.JWTUtil;
import de.lunx.auth.User;
import de.lunx.data.DataManager;
import de.lunx.data.EncryptUtil;
import de.lunx.querying.TQuery;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class HttpServer {
    private final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
    private final HttpStatus internalError = HttpStatus.INTERNAL_SERVER_ERROR;
    private final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
    private final Gson GSON = new Gson();

    public void startServer() {
        var app = Javalin.create()
                .get("/ping", ctx -> ctx.result("pong"))
                .post("/auth", ctx -> {
                    String authBasic = ctx.header("Authorization");
                    if (authBasic == null || !authBasic.startsWith("Basic ")) {
                        ctx.status(unauthorized); // 403
                        ctx.result(QueryError.error("Invalid credentials", "Please provide a " +
                                "Base64 encrypted username and password."));
                        ctx.skipRemainingHandlers();
                        return;
                    }
                    String base64 = authBasic.substring(6);

                    byte[] base64DecodedBytes = Base64.getDecoder().decode(base64);
                    String decodedString = new String(base64DecodedBytes);

                    User user = AuthManager.getInstance().getUser(decodedString.split(":")[0]);
                    if (user == null) {
                        ctx.result(QueryError.error("Invalid credentials", "User not found."));
                        ctx.status(HttpStatus.UNAUTHORIZED); // 403
                        ctx.skipRemainingHandlers();
                        return;
                    }
                    if (!user.isActive()) {
                        ctx.result(QueryError.error("User inactive", "This user is currently inactive."));
                        ctx.status(HttpStatus.UNAUTHORIZED); // 403
                        ctx.skipRemainingHandlers();
                        return;
                    }
                    String password = decodedString.split(":")[1];
                    if (!user.getHashedPassword().equals(EncryptUtil.hash(password))) {
                        ctx.result(QueryError.error("Invalid credentials", "Wrong password."));
                        ctx.status(HttpStatus.UNAUTHORIZED); // 403
                        ctx.skipRemainingHandlers();
                        return;
                    }

                    JsonObject o = new JsonObject();
                    o.addProperty("success", true);
                    o.addProperty("token", JWTUtil.getInstance().token(user.getUsername()));
                    ctx.result(o.toString());
                })
                .before(ctx -> {
                    if (ctx.url().contains("auth")) return;
                    String token = ctx.header("Authorization");
                    if (token == null) {
                        ctx.result(QueryError.error("Token missing",
                                "Please provide a valid token to use the service."));
                        ctx.status(unauthorized);
                        ctx.skipRemainingHandlers();
                        return;
                    }
                    token = token.replace("Bearer ", "");
                    DecodedJWT decoded = Main.getInstance().getJwt().decode(token);
                    if (decoded.getExpiresAt().before(Date.from(Instant.now()))) {
                        ctx.result(QueryError.error("Token expired",
                                "This token cannot be used. Please request a new one."));
                        ctx.status(unauthorized);
                        ctx.skipRemainingHandlers();
                    }
                })
                .post("/query", ctx -> {
                    String body = ctx.body();
                    TQuery.QueryResult result = TQuery.parse(body);

                    JsonObject o = new JsonObject();
                    o.addProperty("result", result.getType().name());
                    switch (result.getType()) {
                        case SUCCESS -> o.addProperty("rowsChanged", result.getRowsChanged());
                        case RESULT_SET -> o.add("data", GSON.toJsonTree(result.getResultSet()));
                        case FAILED -> o.add("error", GSON.toJsonTree("Error!"));
                    }
                })
                .start();
        log.info("Started HTTP server on port " + DataManager.getInstance().getConfiguration().getPort());
    }
}