package ngo.teog.swift.helpers;

/**
 * Exception that indicates an error during parsing of a server response.
 * @author nitelow
 */

public class ResponseException extends Exception {

    /**
     * Creates a new ResponseException.
     * @param message Message
     */
    public ResponseException(String message) {
        super(message);
    }
}
