package ngo.teog.swift.helpers.data;

import androidx.room.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Definition of "reports" table in Room database and wrapper class for a report.
 * @author nitelow
 */
@Entity(tableName = "reports", primaryKeys = {"id", "device", "hospital"})
public class Report implements  Serializable {

    private int id;
    private final int author;
    private final String title;
    private final int device;
    private int hospital;
    private final int currentState;
    private final String description;

    private final boolean valid;
    private final Date created;
    private Date lastSync;

    public Report(int id, int author, String title, int device, int hospital, int currentState, String description, boolean valid, Date created) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.device = device;
        this.hospital = hospital;
        this.currentState = currentState;
        this.description = description;
        this.valid = valid;
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

    public int getCurrentState() {
        return currentState;
    }

    public String getDescription() {
        return description;
    }

    public boolean getValid() {
        return valid;
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
