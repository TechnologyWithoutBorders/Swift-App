package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "groups")
public class Group implements Serializable {
    @PrimaryKey
    private int id;
    private String name;
    private int parentGroup;
    private Date lastUpdate;
    private Date lastSync;

    public Group(int id, String name, int parentGroup, Date lastUpdate) {
        this.id = id;
        this.name = name;
        this.parentGroup = parentGroup;
        this.lastUpdate = lastUpdate;
        this.lastSync = new Date();
    }

    public int getId() {
        return id;
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
