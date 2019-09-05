package ngo.teog.swift.gui.reportCreation;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Report;

public class ReportCreationViewModel extends ViewModel {
    private HospitalRepository hospitalRepo;

    @Inject
    public ReportCreationViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void createReport(Report report, int userId) {
        hospitalRepo.createReport(report, userId);
    }
}
