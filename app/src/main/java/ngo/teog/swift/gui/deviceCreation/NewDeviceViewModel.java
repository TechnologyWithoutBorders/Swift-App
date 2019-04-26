package ngo.teog.swift.gui.deviceCreation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ngo.teog.swift.helpers.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.User;

public class NewDeviceViewModel extends ViewModel {
    private HospitalRepository hospitalRepo;

    @Inject
    public NewDeviceViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void createDevice(HospitalDevice device, int userId) {
        //TODO
    }
}
