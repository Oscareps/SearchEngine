package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;

/**
 * Class representing term surrounded with parenthesis
 */
public class ParenthesisTerm extends Term {

    String term;


    /**
     * Constructor for the ParenthesisTerm class
     * @param term
     * @param doc
     */
    public ParenthesisTerm(String term, Document doc)
    {
        super(doc);
        this.term = term;
    }


    @Override
    public String toString() {
        return term;
    }
}
