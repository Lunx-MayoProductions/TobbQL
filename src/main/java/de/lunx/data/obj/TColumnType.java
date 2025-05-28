package de.lunx.data.obj;

public enum TColumnType {
    INTEGER(true),
    TEXT(true),
    CHAR(false),
    DECIMAL(true),
    FILE(false),
    DATE(false),
    TIME(false),
    DATETIME(false),
    BOOLEAN(false),
    UNIQUE_IDENTIFIER(false),
    ;

    public final boolean useSize;

    TColumnType(boolean useSize) {
        this.useSize = useSize;
    }
}
