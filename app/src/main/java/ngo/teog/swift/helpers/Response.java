package ngo.teog.swift.helpers;

/**
 * @author Julian Deyerler, Technology without Borders
 */

public final class Response {

    //Response Codes

    /**
     * Signalisiert fehlerlose Durchführung.
     */
    public static final int CODE_OK = 0;

    /**
     * Signalisiert einen Fehler bei der Durchführung, der dem Benutzer angezeigt werden soll.
     */
    public static final int CODE_FAILED_VISIBLE = 1;

    /**
     * Signalisiert einen Fehler bei der Durchführung, der dem Benutzer <b>nicht</b> angezeigt werden soll.
     */
    public static final int CODE_FAILED_HIDDEN = 2;

    //Response Struktur

    public static final String CODE_FIELD = "response_code";
    public static final String DATA_FIELD = "data";
}
