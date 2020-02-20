package ngo.teog.swift.gui.hospital;

import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;

import java.util.List;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.data.UserProfileInfo;
import ngo.teog.swift.helpers.export.HospitalDump;

public class PortationViewModel extends ViewModel {
    private HospitalRepository hospitalRepo;
    private LiveData<HospitalDump> hospital;

    @Inject
    public PortationViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void init(int userId) {
        if(this.hospital != null) {
            return;
        }

        hospital = hospitalRepo.getHospitalDump(userId);
    }

    public LiveData<HospitalDump> getHospitalDump() {
        return hospital;
    }
}