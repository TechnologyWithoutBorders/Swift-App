package ngo.teog.swift.helpers.export;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.User;

public class HospitalDump {
    @Embedded
    private Hospital hospital;

    @Relation(parentColumn = "id", entityColumn = "hospital")
    private List<User> users;

    @Relation(parentColumn = "id", entityColumn = "hospital", entity = HospitalDevice.class)
    private List<DeviceDump> devices;
}
