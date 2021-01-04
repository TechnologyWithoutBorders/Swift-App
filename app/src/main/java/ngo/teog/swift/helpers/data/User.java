package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Definition of "users" table in Room database and wrapper class for a user.
 * @author nitelow
 */
//(foreignKeys = @ForeignKey(entity = Hospital.class, parentColumns = "id", childColumns = "hospital", onDelete = CASCADE))
@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private final int id;
    private final String phone;
    private final String mail;
    private final String name;

    private final int hospital;

    private final String position;
    private Date lastUpdate;
    private Date lastSync;

    public User(int id, String phone, String mail, String name, int hospital, String position, Date lastUpdate) {
        this.id = id;
        this.phone = phone;
        this.mail = mail;
        this.name = name;
        this.hospital = hospital;
        this.position = position;
        this.lastUpdate = lastUpdate;
        this.lastSync = new Date();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getMail() {
        return mail;
    }

    public int getHospital() {
        return hospital;
    }

    public String getPosition() {
        return position;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }
}
