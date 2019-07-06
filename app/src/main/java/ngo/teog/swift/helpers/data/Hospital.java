package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "hospitals")
public class Hospital implements Serializable {
    @PrimaryKey
    private int id;
    private String name;
    private String location;
    private float longitude;
    private float latitude;
    private Date lastUpdate;

    public Hospital(int id, String name, String location, float longitude, float latitude, Date lastUpdate) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lastUpdate = lastUpdate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getLocation() {
        return location;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
}
