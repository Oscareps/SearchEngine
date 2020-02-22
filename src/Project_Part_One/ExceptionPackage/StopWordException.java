package Project_Part_One.ExceptionPackage;

/**
 * Exception thrown when StopWord encountered
 */
public class StopWordException extends Exception {

    /**
     * Constructor for StopWordException
     */
    public StopWordException() {
        super("Stopword detected");
    }

}
