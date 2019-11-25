package ngo.teog.swift.communication;

/**
 * Contains some identifiers for referencing segments of a server response.
 * @author nitelow
 */

public final class SwiftResponse {

    //Response Codes

    /** Indicates successful execution of request. */
    public static final int CODE_OK = 0;

    /** Indicates an error during execution of request that should be shown to the user */
    public static final int CODE_FAILED_VISIBLE = 1;

    /** Indicates an error during execution of request that should <b>not</b> be shown to the user */
    public static final int CODE_FAILED_HIDDEN = 2;

    //Response structure

    /** Identifier for the response code */
    public static final String CODE_FIELD = "response_code";

    /** Identifier for the data block */
    public static final String DATA_FIELD = "data";
}
