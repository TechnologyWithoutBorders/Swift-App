package ngo.teog.swift.gui.imageCapture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.UserInfo;

public class ImageCaptureViewModel extends ViewModel {
    private final HospitalRepository hospitalRepo;

    @Inject
    public ImageCaptureViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }
}
