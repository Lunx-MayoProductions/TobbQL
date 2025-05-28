package de.lunx.data;

import java.util.Date;

public class JsonDate {

    private Date date;

    public JsonDate() {
    }

    public JsonDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "JsonDate{" +
                "date=" + date +
                '}';
    }
}
