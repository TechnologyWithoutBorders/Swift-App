package ngo.teog.swift.helpers;

/**
 * Created by Julian on 13.01.2018.
 */

public final class ResponseCode {

    /**
     * Signalisiert fehlerlose Durchführung.
     */
    public static final int OK = 0;

    /**
     * Signalisiert einen Fehler bei der Durchführung, der dem Benutzer angezeigt werden soll.
     */
    public static final int FAILED_VISIBLE = 1;

    /**
     * Signalisiert einen Fehler bei der Durchführung, der dem Benutzer <b>nicht</b> angezeigt werden soll.
     */
    public static final int FAILED_HIDDEN = 2;
}
