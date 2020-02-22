package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;

/**
 * Class representing phone number
 */
public class PhoneTerm extends Term {

    private String phone;


    /**
     * Constructor for the PhoneTerm class
     * @param term String representing the phone number
     * @param doc Project_Part_One.Document holding the term
     */
    public PhoneTerm(String term, Document doc)
    {
        super(doc);
        this.phone = term.replaceAll("\\s", " ");
    }


    @Override
    public String toString() {
        return this.phone;
    }
}
