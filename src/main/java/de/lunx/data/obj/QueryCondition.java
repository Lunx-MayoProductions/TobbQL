package de.lunx.data.obj;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class QueryCondition {
    private transient final TTable table;

    private String column;
    private Object value;

    public QueryCondition(TTable table) {
        this.table = table;
    }

    public boolean check(HashMap<String, Object> row) {
        TColumn c = table.getColumn(column);
        if (c.validate(value)) {
            return value.equals(row.getOrDefault(column, ""));
        }
        return false;
    }
}
