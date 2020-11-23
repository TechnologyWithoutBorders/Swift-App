package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "groups", primaryKeys = {"id", "hospital"})
public class Group implements Serializable {
    private int id;
    private int hospital;
    private String name;
    private int parentGroup;
    private Date lastUpdate;
    private Date lastSync;

    public Group(int id, int hospital, String name, int parentGroup, Date lastUpdate) {
        this.id = id;
        this.name = name;
        this.parentGroup = parentGroup;
        this.lastUpdate = lastUpdate;
        this.lastSync = new Date();
    }

    public int getId() {
        return id;
    }

    public int getHospital() {
        return hospital;
    }

    public String getName() {
        return name;
    }

    public int getParentGroup() {
        return parentGroup;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }
}
