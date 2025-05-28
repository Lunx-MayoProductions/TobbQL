package de.lunx.data;

import com.google.gson.*;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Deprecated(forRemoval = true, since = "0.1.0")
public class JsonTable {

    private final File file;
    private final List<JsonObject> objects = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public JsonTable(File file) {
        this.file = file;
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            save();
        } else {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonArray()) {
                    element.getAsJsonArray().forEach(e -> objects.add(e.getAsJsonObject()));
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void insert(JsonObject object) {
        objects.add(object);
        save();
    }

    public void delete(Predicate<JsonObject> filter) {
        objects.removeIf(filter);
        save();
    }

    public Optional<JsonObject> findOne(Predicate<JsonObject> filter) {
        return objects.stream().filter(filter).findFirst();
    }

    public List<JsonObject> find(Predicate<JsonObject> filter) {
        List<JsonObject> result = new ArrayList<>();
        for (JsonObject object : objects) {
            if (filter.test(object)) result.add(object);
        }
        return result;
    }

    public void update(Predicate<JsonObject> filter, Consumer<JsonObject> updater) {
        for (JsonObject object : objects) {
            if (filter.test(object)) {
                updater.accept(object);
            }
        }
        save();
    }

    public void save() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(objects, writer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
