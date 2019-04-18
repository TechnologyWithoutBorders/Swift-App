package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import ngo.teog.swift.gui.userInfo.UserInfoActivity;
import ngo.teog.swift.helpers.SearchObject;

/**
 * Die User-Klasse kapselt alle Informationen über einen Benutzer. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

//(foreignKeys = @ForeignKey(entity = Hospital.class, parentColumns = "id", childColumns = "hospital", onDelete = CASCADE))

@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey
    private int id;
    private String phone;
    private String mail;
    private String name;

    private int hospital;

    private String position;
    private long lastUpdate;

    public User(int id, String phone, String mail, String name, int hospital, String position, long lastUpdate) {
        this.id = id;
        this.phone = phone;
        this.mail = mail;
        this.name = name;
        this.hospital = hospital;
        this.position = position;
        this.lastUpdate = lastUpdate;
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

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
