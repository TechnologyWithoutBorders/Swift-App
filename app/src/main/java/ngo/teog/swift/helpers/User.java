package ngo.teog.swift.helpers;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Die User-Klasse kapselt alle Informationen über einen Benutzer. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * Created by Julian on 07.11.2017.
 */

public class User implements Serializable {
    private int id;
    private String userName;
    private String phone;
    private String mail;
    private String fullName;
    private String qualifications;

    public User(int id, String userName, String phone, String mail, String fullName, String qualifications) {
        this.id = id;
        this.userName = userName;
        this.phone = phone;
        this.mail = mail;
        this.fullName = fullName;
        this.qualifications = qualifications;
    }

    public String getUserName() {
        return userName;
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

    public String getQualifications() {
        return qualifications;
    }
}
