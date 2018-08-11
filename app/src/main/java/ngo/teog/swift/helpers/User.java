package ngo.teog.swift.helpers;

import java.io.Serializable;

import ngo.teog.swift.gui.UserInfoActivity;

/**
 * Die User-Klasse kapselt alle Informationen über einen Benutzer. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

public class User extends SearchObject {
    private int id;
    private String phone;
    private String mail;
    private String fullName;

    private Hospital hospital;

    private String position;

    public User(int id, String phone, String mail, String fullName, Hospital hospital, String position) {
        this.id = id;
        this.phone = phone;
        this.mail = mail;
        this.fullName = fullName;
        this.hospital = hospital;
        this.position = position;
    }

    public int getID() {
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

    public Hospital getHospital() {
        return hospital;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String getName() {
        return fullName;
    }

    @Override
    public String getInformation() {
        return hospital.getName();
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
