package de.lunx.data.obj;

import de.lunx.data.DataManager;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class TTable {
    private final UUID uniqueID;

    private String name;
    private String dataBase;

    private final List<TColumn> columns = new ArrayList<>();
    private final List<List<Object>> data = new ArrayList<>();

    public TTable(String name, String dataBase) {
        this.name = name;
        this.dataBase = dataBase;
        this.uniqueID = UUID.randomUUID();
    }

    public TTable addColumn(TColumn column) {
        columns.add(column);
        return this;
    }

    public void build() {
        DataManager.getInstance().getDatabase(dataBase).registerTTable(this);
        DataManager.getInstance().save(this);
    }
}