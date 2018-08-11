package ngo.teog.swift.helpers;

import java.util.Date;

/**
 * @author Julian Deyerler
 */

public class NewsItem {
    private int id;
    private Date date;
    private String value;

    public NewsItem(int id, Date date, String value) {
        this.id = id;
        this.date = date;
        this.value = value;
    }

    public int getID() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }
}
