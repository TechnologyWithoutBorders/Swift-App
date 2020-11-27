package ngo.teog.swift.helpers;

public class SynchronisationData {
    private final HospitalInfo hospitalInfo;
    private final int userGroup;

    public SynchronisationData(HospitalInfo hospitalInfo, int userGroup) {
        this.hospitalInfo = hospitalInfo;
        this.userGroup = userGroup;
    }

    public HospitalInfo getHospitalInfo() {
        return hospitalInfo;
    }

    public int getUserGroup() {
        return userGroup;
    }
}
