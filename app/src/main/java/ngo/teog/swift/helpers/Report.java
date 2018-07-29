package ngo.teog.swift.helpers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Die Report-Klasse kapselt alle Informationen über einen Report. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

public class Report implements Serializable {

    public static final SimpleDateFormat reportFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int id;
    private int author;
    private String authorName;
    private int device;
    private int previousState;
    private int currentState;
    private String description;
    private Date dateTime;

    public Report(int id, int author, String authorName, int device, int previousState, int currentState, String description, Date dateTime) {
        this.id = id;
        this.author = author;
        this.authorName = authorName;
        this.device = device;
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

    public String getAuthorName() {
        return authorName;
    }

    public int getDevice() {
        return device;
    }

    public int getPreviousState() {
        return previousState;
    }

    public int getCurrentState() {
        return currentState;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateTime() {
        return dateTime;
    }
}
