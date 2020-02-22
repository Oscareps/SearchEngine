package Project_Part_One.ExceptionPackage;

/**
 *  Exception thrown when illegal word is created
 */
public class IllegalWordException extends Exception {

    String illegalWord;

    /**
     * Constructor for the exception
     * @param illegalWord
     */
    public IllegalWordException (String illegalWord)
    {
        super();
        this.illegalWord = illegalWord;
    }

    /**
     * Getter for the illegal word
     * @return The Illegal word
     */
    public String getIllegalWord()
    {
        return illegalWord;
    }
}
