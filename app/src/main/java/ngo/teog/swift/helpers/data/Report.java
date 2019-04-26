package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Die Report-Klasse kapselt alle Informationen über einen Report. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

@Entity(tableName = "reports")
public class Report implements  Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int author;
    private int device;
    private int previousState;
    private int currentState;
    private String description;
    private long created;

    public Report(int id, int author, int device, int previousState, int currentState, String description, long created) {
        this.id = id;
        this.author = author;
        this.device = device;
        this.previousState = previousState;
        this.currentState = currentState;
        this.description = description;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public int getAuthor() {
        return author;
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
