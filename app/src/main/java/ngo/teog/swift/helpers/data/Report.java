package ngo.teog.swift.helpers.data;

import androidx.room.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Die Report-Klasse kapselt alle Informationen über einen Report. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

@Entity(tableName = "reports", primaryKeys = {"id", "device"})
public class Report implements  Serializable {

    private int id;
    private int author;
    private int device;
    private int previousState;
    private int currentState;
    private String description;
    private Date created;
    private Date lastSync;

    public Report(int id, int author, int device, int previousState, int currentState, String description, Date created) {
        this.id = id;
        this.author = author;
        this.device = device;
        this.previousState = previousState;
        this.currentState = currentState;
        this.description = description;
        this.created = created;
        this.lastSync = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getCreated() {
        return created;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }
}
