package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;


/**
 * Class for term with hyphen
 */
public class HyphenWordTerm extends Term{

    private String wordsInTerm;


    /**
     * Constructor for the HyphenTerm class
     * @param hyphenWordTerm String representing the term
     * @param doc Project_Part_One.Document holding the term
     * @throws IllegalArgumentException
     */
    public HyphenWordTerm(String hyphenWordTerm, Document doc) throws IllegalArgumentException {
        super(doc);
        this.wordsInTerm = hyphenWordTerm;
    }


    @Override
    public String toString() {
        return wordsInTerm;
    }
}
