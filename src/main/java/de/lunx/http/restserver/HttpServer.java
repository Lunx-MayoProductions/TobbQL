package de.lunx.http.restserver;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.lunx.Main;
import de.lunx.auth.AuthManager;
import de.lunx.data.DataManager;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;

@Slf4j
public class HttpServer {
    private final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
    private final HttpStatus internalError = HttpStatus.INTERNAL_SERVER_ERROR;
    private final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
    private final Gson GSON = new Gson();

    public void startServer() {
        Javalin app = Javalin.create()
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
                    //TODO: Rework parsing
                })
                .start(25544);
        log.info("Started HTTP server on port 25544");
    }
}