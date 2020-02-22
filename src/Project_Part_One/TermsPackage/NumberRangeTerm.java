package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;

public class NumberRangeTerm extends Term {

    private NumberTerm leftSide;
    private NumberTerm rightSide;

    /**
     * This constructor is for terms in the form:
     * 1.Number-number (for example: 6-7)
     * 2.Between number and number (for example: between 18 and 24)
     *
     * @param numRangeTerm
     * @param doc
     * @throws IllegalArgumentException
     */
    public NumberRangeTerm(String numRangeTerm, Document doc) throws IllegalArgumentException {
        super(doc);
        // Number-Number
        if (numRangeTerm.contains("-")) {
            String[] termSplit = numRangeTerm.split("-");
            this.leftSide = new NumberTerm(termSplit[0], doc);
            this.rightSide = new NumberTerm(termSplit[1], doc);
        }
        //Number and Number
        else {
            String[] termSplit = numRangeTerm.split(" ");
            this.leftSide = new NumberTerm(termSplit[1], doc);
            this.rightSide = new NumberTerm(termSplit[3], doc);
        }

    }


    @Override
    public String toString() {
        return this.leftSide + "-" + this.rightSide;
    }

}