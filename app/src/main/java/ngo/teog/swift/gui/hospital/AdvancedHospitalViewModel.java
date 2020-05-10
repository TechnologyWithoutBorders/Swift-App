package ngo.teog.swift.gui.hospital;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ngo.teog.swift.R;
import ngo.teog.swift.communication.BaseResponseListener;
import ngo.teog.swift.communication.DefaultRequest;
import ngo.teog.swift.communication.RequestFactory;
import ngo.teog.swift.communication.UserAction;
import ngo.teog.swift.communication.VolleyManager;
import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalRepository;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.User;

public class AdvancedHospitalViewModel extends ViewModel {
    private HospitalRepository hospitalRepo;

    @Inject
    public AdvancedHospitalViewModel(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    public void createUser(User user, int userId) {

    }
}