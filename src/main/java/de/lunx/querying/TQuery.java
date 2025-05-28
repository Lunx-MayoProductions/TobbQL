package de.lunx.querying;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import static de.lunx.Main.printStackTraceLevel;

@Slf4j
public class TQuery {
    public static QueryResult parse(String json) {
        JsonObject o = JsonParser.parseString(json).getAsJsonObject();
        Type type = null;
        try {
            type = Type.valueOf(o.get("type").getAsString());
        } catch (IllegalArgumentException ex) {
            printStackTraceLevel(log, System.Logger.Level.DEBUG, ex);
            return new QueryResult(QueryResultType.UNKNOWN_ACTION, 0);
        }
        switch (type) {
            case GET_DATA -> {
                //TODO: Fetch data
            }
            case CREATE_DATABASE -> {
                //TODO: Create database
            }
        }

        //TODO: add other types
        return new QueryResult(QueryResultType.FAILED, 0);
    }

    public record QueryResult(QueryResultType type, int rowsChanged) {}

    public enum QueryResultType {
        SUCCESS,
        RESULT_SET,
        FAILED,
        ALREADY_EXISTS,
        UNKNOWN_DB,
        UNKNOWN_TABLE,
        UNKNOWN_ACTION
    }

    public enum Type {
        GET_DATA,
        INSERT_DATA,
        UPDATE_DATA,
        DELETE_DATA,
        CREATE_TABLE,
        DELETE_TABLE,
        CLEAR_TABLE,
        GRANT_PERMISSION,
        CREATE_DATABASE,
        DELETE_DATABASE,

        CREATE_USER,
        DEACTIVATE_USER,
        EDIT_USER,
        DELETE_USER,
    }
}