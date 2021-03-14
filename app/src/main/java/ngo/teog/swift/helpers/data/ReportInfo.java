package ngo.teog.swift.helpers.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

/**
 * Wraps all information regarding a report. Contains the report itself, its author(s) and the corresponding hospital.
 * @author nitelow
 */
public class ReportInfo implements Serializable {
    @Embedded
    private Report report;

    @Relation(parentColumn = "author", entityColumn = "id")
    private User author;

    @Relation(parentColumn = "hospital", entityColumn = "id")
    private Hospital hospital;

    public ReportInfo(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Hospital getHospital() {
        return hospital;
    }
}
