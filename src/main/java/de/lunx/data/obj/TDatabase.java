package de.lunx.data.obj;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TDatabase {
    @Getter
    private String name;
    private final List<UUID> tables = new ArrayList<>();

    private transient final List<TTable> tableOBJs = new ArrayList<>();
    @Getter
    private final Charset charset;

    public TDatabase(String name, Charset charset) {
        this.name = name;
        this.charset = charset;
    }

    public void registerTTable(TTable tTable) {
        if (!tableOBJs.contains(tTable)) tableOBJs.add(tTable);
        if (!tables.contains(tTable.getUniqueID())) tables.add(tTable.getUniqueID());
    }

    public void registerOrUpdateTTable(TTable tTable) {
        if (!tableOBJs.contains(tTable)) tableOBJs.add(tTable);
        else {
            tableOBJs.remove(tTable);
            tableOBJs.add(tTable);
        }
        if (!tables.contains(tTable.getUniqueID())) tables.add(tTable.getUniqueID());
    }

    public List<TTable> getTables() {
        return tableOBJs;
    }

    public List<UUID> getTablesRaw() {
        return tables;
    }

    @Nullable
    public TTable getTable(String name) {
        for (TTable t : tableOBJs) if (t.getName().equals(name)) return t;
        return null;
    }

    public boolean deleteTable(String name) {
        TTable t = getTable(name);
        if (t == null) return false;
        tables.remove(t.getUniqueID());
        tableOBJs.remove(t);
        return true;
    }

    public boolean hasTable(String name) {
        for (TTable t : tableOBJs) if (t.getName().equals(name)) return true;
        return false;
    }
}