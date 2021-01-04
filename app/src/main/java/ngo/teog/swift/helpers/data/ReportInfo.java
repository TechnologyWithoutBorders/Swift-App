package ngo.teog.swift.helpers.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

/**
 * Wraps all information regarding a report. Contains the report itself, its author(s) and the corresponding hospital.
 * @author nitelow
 */
public class ReportInfo implements Serializable {
    @Embedded
    private Report report;

    @Relation(parentColumn = "author", entityColumn = "id")
    private List<User> authors;

    @Relation(parentColumn = "hospital", entityColumn = "id")
    private List<Hospital> hospitals;

    public ReportInfo(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public List<User> getAuthors() {
        return authors;
    }

    public void setAuthors(List<User> authors) {
        this.authors = authors;
    }

    public void setHospitals(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }
}
