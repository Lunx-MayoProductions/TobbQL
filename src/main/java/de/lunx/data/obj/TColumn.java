package de.lunx.data.obj;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class TColumn {
    private final TTable tTable;
    private String name;
    private TColumnType type;

    private Object defaultValue;

    private int size;
    private UUID fileID;
    private int decimals;
    private boolean autoIncrement;
    private int incrementValue;
    private boolean notNull;
    private boolean unique;

    public TColumn(TTable tTable, String name, TColumnType type) {
        this.tTable = tTable;
        this.name = name;
        this.type = type;
    }

    public boolean validate(Object value) {
        switch (type) {
            case INTEGER -> {
                return value instanceof Integer;
            }
            case TEXT -> {
                return value instanceof String;
            }
            case DECIMAL -> {
                return value instanceof Double;
            }
            case UNIQUE_IDENTIFIER -> {
                if (value instanceof String v) {
                    try {
                        UUID.fromString(v);
                        return true;
                    } catch (IllegalArgumentException ignored) {return false;}
                }
                return false;
            }
            case BOOLEAN -> {
                return value instanceof Boolean;
            }
            default -> {
                return false;
            }
            //TODO: Add other data types
        }
    }

    public TColumn size(int size) {
        if (!type.useSize) throw new IllegalStateException("You may only use size property with column types supporting it.");
        this.size = size;
        return this;
    }
    public TColumn decimal(int decimal) {
        if (!type.equals(TColumnType.DECIMAL)) throw new IllegalStateException("Type must be DECIMAL to use decimals.");
        this.decimals = decimal;
        return this;
    }

    public TColumn autoIncrement(boolean increase, int increment) {
        if (!type.equals(TColumnType.INTEGER) && !type.equals(TColumnType.DECIMAL))
            throw new IllegalStateException("You may only use auto increment property with INTEGER or DECIMAL.");
        autoIncrement = increase;
        incrementValue = increment;
        return this;
    }

    public TColumn unique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public TColumn notNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }

    public TColumn defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}