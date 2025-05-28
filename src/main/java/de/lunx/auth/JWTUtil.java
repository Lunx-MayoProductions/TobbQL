package de.lunx.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import de.lunx.Main;
import de.lunx.data.DataManager;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import static de.lunx.Main.printStackTrace;

@Slf4j
public class JWTUtil {
    private String secret;
    private final SecureRandom random = new SecureRandom();

    private final JWTVerifier verifier;

    public JWTUtil(String secret) {
        this.secret = secret;
        if (secret.isBlank()) {
            log.warn("JWT secret cannot be empty! Generating a new one...");
            this.secret = generateSecret(30);
            DataManager.getInstance().getConfiguration().setJwtSecret(this.secret);
            DataManager.getInstance().saveConfig();
        }
        verifier = JWT.require(Algorithm.HMAC256(this.secret)).build();
    }

    public static JWTUtil getInstance() {
        return Main.getInstance().getJwt();
    }

    public String generateSecret(int length) {
        StringBuilder characterPool = new StringBuilder();
        String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        characterPool.append(LETTERS);
        String DIGITS = "0123456789";
        characterPool.append(DIGITS);
        String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        characterPool.append(SYMBOLS);

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterPool.length());
            result.append(characterPool.charAt(index));
        }

        return result.toString();
    }

    public String token(String username) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(
                convertFromChronoUnit(DataManager.getInstance().getConfiguration().getTokenExpirationUnit()),
                DataManager.getInstance().getConfiguration().getTokenExpirationTimeInterval());
        Date expiration = calendar.getTime();
        return JWT.create()
                .withSubject(username)
                .withIssuer("tobbql")
                .withNotBefore(now)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT decode(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            printStackTrace(log, e);
            return null;
        }
    }

    public boolean verify(String token) {
        DecodedJWT decodedJWT = decode(token);
        return decodedJWT.getIssuer().equals("tobbql");
    }

    private int convertFromChronoUnit(ChronoUnit chronoUnit) {
        return switch (chronoUnit) {
            case MILLIS      -> Calendar.MILLISECOND;
            case SECONDS     -> Calendar.SECOND;
            case MINUTES     -> Calendar.MINUTE;
            case HOURS       -> Calendar.HOUR;
            case DAYS        -> Calendar.DAY_OF_YEAR;
            case WEEKS       -> Calendar.WEEK_OF_YEAR;
            case MONTHS      -> Calendar.MONTH;
            case YEARS, MILLENNIA, DECADES, CENTURIES -> Calendar.YEAR;
            case ERAS        -> Calendar.ERA;
            default -> throw new IllegalArgumentException("Unsupported ChronoUnit: " + chronoUnit);
        };
    }

}
