package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;


/**
 * Class representing percent term
 */
public class PercentTerm extends Term {

    private String numberPercent;


    /**
     * Constructor of PercentTerm object
     * @param percentTerm String represents the percent term
     * @param doc the doc which holds this term
     */
    public PercentTerm(String percentTerm, Document doc)
    {
        super(doc);
        if(percentTerm.contains("%")) { //Rule Percent.1 number%
            numberPercent = percentTerm.substring(0,percentTerm.length()-1); // set the number value to afterDot
        }
        else { //Rule Percent.2 number percent, //Rule Percent.3 number percentage
            String[] percentSplit = percentTerm.split("\\s");
            numberPercent = percentSplit[0]; // set the number value to afterDot
        }

    }


    @Override
    public String toString()
    {
        return this.numberPercent + "%";

    }
}
