package ngo.teog.swift.helpers;

import java.io.Serializable;

/**
 * Die User-Klasse kapselt alle Informationen über einen Benutzer. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

public class User implements Serializable {
    private int id;
    private String phone;
    private String mail;
    private String fullName;
    private String qualifications;

    public User(int id, String phone, String mail, String fullName, String qualifications) {
        this.id = id;
        this.phone = phone;
        this.mail = mail;
        this.fullName = fullName;
        this.qualifications = qualifications;
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

    public String getQualifications() {
        return qualifications;
    }
}
