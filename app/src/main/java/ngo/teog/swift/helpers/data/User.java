package ngo.teog.swift.helpers.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

import ngo.teog.swift.gui.UserInfoActivity;
import ngo.teog.swift.helpers.SearchObject;

/**
 * Die User-Klasse kapselt alle Informationen über einen Benutzer. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

@Entity
public class User extends SearchObject {
    @PrimaryKey
    private int id;
    private String phone;
    private String mail;
    private String fullName;

    private int hospital;

    private String position;
    private long lastUpdate;

    public User(int id, String phone, String mail, String fullName, int hospital, String position) {
        this.id = id;
        this.phone = phone;
        this.mail = mail;
        this.fullName = fullName;
        this.hospital = hospital;
        this.position = position;
        this.lastUpdate = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getMail() {
        return mail;
    }

    public String getFullName() {
        return fullName;
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

    @Override
    public String getName() {
        return fullName;
    }

    @Override
    public String getInformation() {
        return "Information";
    }

    @Override
    public Class<?> getInfoActivityClass() {
        return UserInfoActivity.class;
    }

    @Override
    public String getExtraIdentifier() {
        return "user";
    }
}
