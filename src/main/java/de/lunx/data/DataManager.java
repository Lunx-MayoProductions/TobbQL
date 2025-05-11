package de.lunx.data;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {
    private final File baseDirectory;
    private final Map<String, JsonTable> tables = new ConcurrentHashMap<>();

    private DataManager(File baseDirectory) {
        this.baseDirectory = baseDirectory;
        if (!baseDirectory.exists()) baseDirectory.mkdirs();
    }

    public static DataManager create(File file) {
        return new DataManager(file);
    }

    public JsonTable getTable(String name) {
        return tables.computeIfAbsent(name, key -> new JsonTable(new File(baseDirectory, key + ".json")));
    }

    public JsonTable createTable(String name) {
         JsonTable table = new JsonTable(new File(baseDirectory, name + ".json"));
         tables.put(name, table);
         return table;
    }
}
