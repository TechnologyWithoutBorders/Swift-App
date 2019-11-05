package ngo.teog.swift.helpers;

/**
 * Exception that indicates an error during parsing of a server response.
 * @author nitelow
 */

public class TransparentServerException extends Exception {

    /**
     * Creates a new TransparentResponseException.
     * @param message Message
     */
    public TransparentServerException(String message) {
        super(message);
    }
}
