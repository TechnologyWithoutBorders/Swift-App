package ngo.teog.swift.communication;

/**
 * Constants for specifying which action the client wants to perform in combination with the server.
 * @author nitelow
 */
public final class DataAction {
    //General actions

    /** Client wants to synchronize changes in hospital data with the server (bidirectinally). */
    public static final String SYNC_HOSPITAL_INFO = "sync_hospital_info";

    //Device actions

    /** Client wants to download a device image from the server */
    public static final String FETCH_DEVICE_IMAGE = "fetch_device_image";

    /** Client wants to retrieve the hash value of a device image */
    public static final String FETCH_DEVICE_IMAGE_HASH = "fetch_device_image_hash";

    /** Client wants to upload a device image to the server. */
    public static final String UPLOAD_DEVICE_IMAGE = "upload_device_image";

    //User actions

    /** Client wants the server to check the credentials of a user */
    public static final String LOGIN_USER = "login_user";

    /** Client wants the server to reset the password of a user */
    public static final String RESET_PASSWORD = "reset_password";

    /** Client wants the server to create a new user */
    public static final String CREATE_USER = "create_user";

    public static final String ACCESS_STORAGE = "access_storage";
}
