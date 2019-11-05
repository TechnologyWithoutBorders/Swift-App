package ngo.teog.swift.helpers;

/**
 * Exception that indicates an error during parsing of a server response.
 * @author nitelow
 */

public class ServerException extends Exception {

    /**
     * Creates a new InvisibleResponseException.
     * @param message Message
     */
    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable e) {
        super(e);
    }
}
