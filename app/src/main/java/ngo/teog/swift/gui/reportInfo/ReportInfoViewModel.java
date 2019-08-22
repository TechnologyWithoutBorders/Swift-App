package ngo.teog.swift.gui.reportInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.ReportInfo;

public class ReportInfoViewModel extends ViewModel {
    private HospitalRepository hospitalRepo;
    private LiveData<ReportInfo> reportInfo;

    public void init(int userId, int deviceId, int reportId) {
        if(this.reportInfo != null) {
            // ViewModel is created on a per-Fragment basis, so the userId
            // doesn't change.
            return;
        }

        reportInfo = hospitalRepo.loadReportInfo(userId, deviceId, reportId);
    }

    @Inject
    public ReportInfoViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public LiveData<ReportInfo> getReportInfo() {
        return reportInfo;
    }
}
