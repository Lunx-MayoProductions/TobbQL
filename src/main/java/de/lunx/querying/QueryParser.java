package de.lunx.querying;

import com.google.gson.*;
import de.lunx.data.DataManager;

import java.io.File;
import java.util.*;

public class QueryParser {

    private final DataManager manager;

    public QueryParser(DataManager manager) {
        this.manager = manager;
    }

    public JsonElement parse(String query) {
        query = query.trim();
        if (!query.endsWith(";")) throw new IllegalArgumentException("Query must end with ';'");
        query = query.substring(0, query.length() - 1); // remove semicolon

        if (query.startsWith("VALUE IF")) {
            return handleValueIf(query.substring(8).trim());
        }

        if (query.startsWith("VALUE")) {
            return handleValue(query.substring(6).trim());
        }

        if (query.startsWith("KEY")) {
            return handleKey(query.substring(4).trim());
        }

        if (query.startsWith("UPDATE")) {
            return handleUpdate(query.substring(7).trim());
        }

        if (query.startsWith("DELETE")) {
            return handleDelete(query.substring(7).trim());
        }

        if (query.startsWith("CREATE DATABASE")) {
            return handleCreateDatabase(query.substring(16).trim());
        }

        if (query.startsWith("CREATE TABLE")) {
            return handleCreateTable(query.substring(13).trim());
        }

        if (query.startsWith("CREATE FILE_STORAGE")) {
            return handleCreateFileStorage(query.substring(20).trim());
        }

        throw new IllegalArgumentException("Unknown query: " + query);
    }

    private JsonElement handleValue(String key) {
        return new Gson().toJsonTree(
                manager.getTable("default")
                        .findOne(obj -> obj.has(key))
                        .map(obj -> obj.get(key))
                        .orElse(null)
        );
    }

    private JsonElement handleValueIf(String condition) {
        String[] parts = condition.split("=");
        String key = parts[0].trim();
        String value = parts[1].trim();
        return new Gson().toJsonTree(
                manager.getTable("default")
                        .find(obj -> value.equals(obj.get(key).getAsString()))
        );
    }

    private JsonElement handleKey(String content) {
        String[] parts = content.split(",", 2);
        String key = parts[0].trim();
        String value = parts[1].trim();

        JsonObject obj = new JsonObject();
        obj.add(key, JsonParser.parseString(value));
        manager.getTable("default").insert(obj);

        return obj;
    }

    private JsonElement handleUpdate(String args) {
        String[] parts = args.split("KEY");
        String oldKey = parts[0].trim();
        String newKey = parts[1].trim();

        manager.getTable("default").update(obj -> obj.has(oldKey), obj -> {
            JsonElement val = obj.remove(oldKey);
            obj.add(newKey, val);
        });

        return new JsonPrimitive("Updated key " + oldKey + " to " + newKey);
    }

    private JsonElement handleDelete(String key) {
        manager.getTable("default").delete(obj -> obj.has(key));
        return new JsonPrimitive("Deleted all with key: " + key);
    }

    private JsonElement handleCreateDatabase(String name) {
        File dir = new File("data/" + name);
        if (dir.mkdirs()) {
            return new JsonPrimitive("Database created: " + name);
        }
        return new JsonPrimitive("Failed to create or already exists: " + name);
    }

    private JsonElement handleCreateTable(String name) {
        return new JsonPrimitive("Table created: " + name);
    }

    private JsonElement handleCreateFileStorage(String name) {
        File f = new File("filestorage/" + name);
        if (f.mkdirs()) {
            return new JsonPrimitive("File storage created: " + name);
        }
        return new JsonPrimitive("Already exists or failed: " + name);
    }
}

