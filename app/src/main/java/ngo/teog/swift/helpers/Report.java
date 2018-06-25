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
    private int previousState;
    private int currentState;
    private String description;
    private Date dateTime;

    public Report(int id, int author, int device, String title, int previousState, int currentState, String description, Date dateTime) {
        this.id = id;
        this.author = author;
        this.device = device;
        this.title = title;
        this.previousState = previousState;
        this.currentState = currentState;
        this.description = description;
        this.dateTime = dateTime;
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
