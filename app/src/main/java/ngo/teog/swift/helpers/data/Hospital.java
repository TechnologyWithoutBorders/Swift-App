package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "hospitals")
public class Hospital implements Serializable {
    @PrimaryKey
    private int id;
    private String name;
    private String location;
    private Date lastUpdate;

    public Hospital(int id, String name, String location, Date lastUpdate) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.lastUpdate = lastUpdate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
}
