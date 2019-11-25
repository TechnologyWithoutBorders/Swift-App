package ngo.teog.swift.communication;

/**
 * Exception that indicates an error during parsing of a server response.
 * @author nitelow
 */

public class ServerException extends Exception {

    /**
     * Creates a new ServerException.
     * @param message Message
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Creates a new ServerException that wraps the given throwable.
     * @param e Throwable
     */
    public ServerException(Throwable e) {
        super(e);
    }
}
