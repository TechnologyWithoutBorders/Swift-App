package ngo.teog.swift.gui;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;

public class ImageCaptureViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;

    @Inject
    public ImageCaptureViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void updateDeviceImage(int deviceId, int userId) {
        hospitalRepo.updateDeviceImage(deviceId, userId);
    }
}
