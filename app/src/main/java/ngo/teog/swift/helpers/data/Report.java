package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Die Report-Klasse kapselt alle Informationen über einen Report. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

@Entity
public class Report implements Serializable {

    @PrimaryKey
    private int id;
    private int author;
    private String authorName;
    private int device;
    private int previousState;
    private int currentState;
    private String description;
    private long created;

    public Report(int id, int author, String authorName, int device, int previousState, int currentState, String description, long created) {
        this.id = id;
        this.author = author;
        this.authorName = authorName;
        this.device = device;
        this.previousState = previousState;
        this.currentState = currentState;
        this.description = description;
        this.created = created;
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

    public long getCreated() {
        return created;
    }
}
