package ngo.teog.swift.helpers.data;

import androidx.room.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Die Report-Klasse kapselt alle Informationen über einen Report. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

@Entity(tableName = "reports", primaryKeys = {"id", "device"})//TODO hospital muss auch in primary keys
public class Report implements  Serializable {

    private int id;
    private int author;
    private String title;
    private int device;
    private int hospital;
    private int previousState;
    private int currentState;
    private String description;
    private Date created;
    private Date lastSync;

    public Report(int id, int author, String title, int device, int hospital, int previousState, int currentState, String description, Date created) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.device = device;
        this.hospital = hospital;
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

    public void setHospital(int hospital) {
        this.hospital = hospital;
    }

    public int getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public int getDevice() {
        return device;
    }

    public int getHospital() {
        return hospital;
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
