package ngo.teog.swift.communication;

/**
 * Exception that wraps an error message from the server that should be shown to the user.
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
