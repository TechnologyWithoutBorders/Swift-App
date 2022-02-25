package ngo.teog.swift.helpers.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Definition of "imageUploadJobs" table in Room database and wrapper class for an image upload job.
 * @author nitelow
 */
@Entity(tableName = "image_upload_jobs")
public class ImageUploadJob implements Serializable {
    @PrimaryKey
    private final int deviceId;
    private Date created;

    public ImageUploadJob(int deviceId) {
        this.deviceId = deviceId;
        this.created = new Date();
    }

    public int getDeviceId() {
        return deviceId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
