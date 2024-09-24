package ngo.teog.swift.gui.deviceCreation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.OrganizationalUnit;

public class NewDeviceViewModel2 extends ViewModel {
    private LiveData<List<DeviceInfo>> deviceInfos;
    private LiveData<List<OrganizationalUnit>> orgUnits;
    private final HospitalRepository hospitalRepo;

    @Inject
    public NewDeviceViewModel2(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.deviceInfos != null) {
            return;
        }

        //do not sync as data is only used for making suggestions for text fields
        deviceInfos = hospitalRepo.loadHospitalDevices(userId, false);
        orgUnits = hospitalRepo.loadOrgUnits(userId);
    }

    /**
     * Returns a snapshot of device infos which will <b>not be up-to-date in all cases</b>
     * @return snapshot of device infos
     */
    public LiveData<List<DeviceInfo>> getDeviceInfos() {
        return deviceInfos;
    }

    public LiveData<List<OrganizationalUnit>> getOrgUnits() {
        return orgUnits;
    }
}