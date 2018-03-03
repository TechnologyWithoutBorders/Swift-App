package ngo.teog.swift.helpers;

import java.io.Serializable;
import java.util.Date;

/**
 * Die Report-Klasse kapselt alle Informationen über einen Report. Sie
 * ist serializable, damit man sie innerhalb eines Intents übergeben kann.
 * @author Julian Deyerler
 */

public class Report implements Serializable {
    private int id;
    private User author;
    private HospitalDevice device;
    private Date date;

    public Report(int id, User author, HospitalDevice device, Date date) {
        this.id = id;
        this.author = author;
        this.device = device;
        this.date = date;
    }

    public int getID() {
        return id;
    }

    public HospitalDevice getDevice() {
        return device;
    }

    public Date getDate() {
        return date;
    }

    public User getAuthor() {
        return author;
    }
}
