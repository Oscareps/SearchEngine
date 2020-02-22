package Project_Part_One.TermsPackage;
import Project_Part_One.Document.Document;


/**
 * Class for each word in the document
 */
public class Term {

    private String docNo;
    private int numOfOccurrences;


    /**
     * Constructor for Term class
     */
    public Term()
    {
        numOfOccurrences = 1;
    }

    /**
     * Constructor of Term object
     * @param doc the ID of tf the document which the term is in
     * @throws IllegalArgumentException
     */
    public Term(Document doc) throws IllegalArgumentException
    {
        this();

        if(doc == null)
            throw new IllegalArgumentException("Project_Part_One.Document is not valid");

        this.docNo = doc.getDocNo().toString();
        this.numOfOccurrences = 1;
    }


    /**
     * Getter for the NumOfOccurrences variable
     * @return The number of times this term appeared
     */
    public int getNumOfOccurrences() {
        return numOfOccurrences;
    }


    /**
     * Adding an occurrence of this term
     */
    public void addOccurrence()
    {
        this.numOfOccurrences++;
    }


    /**
     * Getter for the DocNo of the document holding this term
     * @return
     */
    public String getDocNo() {
        return docNo;
    }
}
