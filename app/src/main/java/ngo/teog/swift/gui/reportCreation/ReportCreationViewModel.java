package ngo.teog.swift.gui.reportCreation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
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
