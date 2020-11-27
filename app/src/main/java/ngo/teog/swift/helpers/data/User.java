package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Die User-Klasse kapselt alle Informationen über einen Benutzer. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author nitelow
 */

//(foreignKeys = @ForeignKey(entity = Hospital.class, parentColumns = "id", childColumns = "hospital", onDelete = CASCADE))

@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)//TODO warum autogenerate? -> evtl. weil man angedacht war, User Accounts lokal zu erzeugen
    private int id;
    private String phone;
    private String mail;
    private String name;

    private int hospital;
    private int group;

    private String position;
    private Date lastUpdate;
    private Date lastSync;

    public User(int id, String phone, String mail, String name, int hospital, int group, String position, Date lastUpdate) {
        this.id = id;
        this.phone = phone;
        this.mail = mail;
        this.name = name;
        this.hospital = hospital;
        this.group = group;
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

    public int getGroup() {
        return group;
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
