package ngo.teog.swift.helpers;

import java.io.Serializable;
import java.util.Date;

/**
 * Die Report-Klasse kapselt alle Informationen über einen Report. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

public class Report implements Serializable {
    private int id;
    private int author;
    private int device;
    private String title;

    public Report(int id, int author, int device, String title) {
        this.id = id;
        this.author = author;
        this.device = device;
        this.title = title;
    }

    public int getID() {
        return id;
    }

    public int getAuthor() {
        return author;
    }

    public int getDevice() {
        return device;
    }

    public String getTitle() {
        return title;
    }
}
