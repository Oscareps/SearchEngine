package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberTerm extends Term {

    private String numberString;
    private boolean isNegative;

    /**
     * This is the constructor for Number Term
     * The is 3 Main rules, and sub rules.
     * Price1.a - for numbers above than  1000
     * Price1.b- for number above than 1M
     * Price1.c - for number above than 1B
     * Price2 - number less than 1000
     * Number will to be round to 3 decimal points
     *
     * @param numberTerm String representing the number found in the text
     * @param doc The document containing the word
     * @throws IllegalArgumentException
     */
    public NumberTerm(String numberTerm, Document doc) throws IllegalArgumentException {
        super(doc);

        //<editor-fold desc="Checking if negative number">
        if(numberTerm.charAt(0) == '-') {
            isNegative = true;
            numberTerm = numberTerm.substring(1);
        }
        else
            isNegative = false;
        //</editor-fold>

        numberTerm = numberTerm.toLowerCase();
        numberTerm = numberTerm.replaceAll(",","");
        String[] numberSplit = numberTerm.split("[\\s-]");
        //Rule Number.2 NumberTerm with fraction , need to be tested!!!!
        if (numberTerm.contains("/")) {
            setNumberWithFraction(numberSplit);
        }
        //Rule Number.1 NumberTerm with  Thousand/Million/Billion
        else if (numberSplit.length == 2 || numberSplit[0].contains("m") || numberSplit[0].contains("bn")) {
            setNumberWithExp(numberSplit);
        } else {//numberTerm only with numbers
            setNumberWithoutExp(numberTerm);
        }
    }


    /**
     * Hanles numbers with fraction inside them
     * @param numWithFrac The number to handle
     */
    private void setNumberWithFraction(String[] numWithFrac)
    {
        String[] fracElements = numWithFrac[1].split("/");
        double doubleWithoutFrac = Double.parseDouble(numWithFrac[0]);
        double fraction = 0;

        if(numWithFrac.length == 3) {
            fraction = Double.parseDouble(fracElements[0]) / Double.parseDouble(fracElements[1]);
            double doubleWithFrac = doubleWithoutFrac + fraction;
            String[] toSet = {""+doubleWithFrac, numWithFrac[2]};
            setNumberWithExp(toSet);
        }
        else if(numWithFrac[1].contains("m")) {
            fraction = Double.parseDouble(fracElements[0]) / Double.parseDouble(fracElements[1].substring(0,fracElements[1].length()-1));
            double doubleWithFrac = doubleWithoutFrac + fraction;
            String[] toSet = {""+doubleWithFrac+"m"};
            setNumberWithExp(toSet);
        }
        else if(numWithFrac[1].contains("bn")) {
            fraction = Double.parseDouble(fracElements[0]) / Double.parseDouble(fracElements[1].substring(0,fracElements[1].length()-2));
            double doubleWithFrac = doubleWithoutFrac + fraction;
            String[] toSet = {""+doubleWithFrac+"bn"};
            setNumberWithExp(toSet);
        }
        else {
            if(doubleWithoutFrac < 1000)
                this.numberString = numWithFrac[0] + " " + numWithFrac[1];
            else {
                fraction = Double.parseDouble(fracElements[0]) / Double.parseDouble(fracElements[1].substring(0, fracElements[1].length()));
                setNumberWithoutExp(doubleWithoutFrac + fraction + "");
            }
        }




    }


    /**
     * Normalize numberTerm whom has word in it
     * numberTerm with  Thousand/Million/Billion
     *
     * @param numberSplit
     */
    public void setNumberWithExp(String[] numberSplit) {
        if(numberSplit.length == 1)
        {
            if(numberSplit[0].toLowerCase().charAt(numberSplit[0].length()-1) == 'm')
                this.numberString = numberSplit[0].substring(0,numberSplit[0].length()-1) + "M";
            else
                this.numberString = numberSplit[0].substring(0,numberSplit[0].length()-2) + "B";
            return;
        }
        if (numberSplit[1].toLowerCase().equals("thousand")) {
            this.numberString = numberSplit[0] + "K";
        } else if (numberSplit[1].toLowerCase().matches("million")) {
            this.numberString = numberSplit[0] + "M";
        } else if (numberSplit[1].toLowerCase().matches("billion")) {
            this.numberString = numberSplit[0] + "B";
        } else if (numberSplit[1].toLowerCase().matches("trillion")) {
            this.numberString = numberSplit[0] + "000B";
        }
    }


    /**
     * Normalize numberTerm contains just number
     * @param numberTerm
     */
    public void setNumberWithoutExp(String numberTerm) {
        double termDoubleValue = Double.parseDouble(numberTerm);

        //Rule 2s
        if (termDoubleValue < 1000) {
            setByFormat(termDoubleValue);
        }
        else if (termDoubleValue < 1000000) {
            setByFormat(termDoubleValue / 1000);
            this.numberString = this.numberString + 'K';

        } else if (termDoubleValue < 1000000000) { //Millions
            setByFormat(termDoubleValue / 1000000);
            this.numberString = this.numberString + 'M';
        }
        else {
            setByFormat(termDoubleValue/1000000000);
            this.numberString = this.numberString+'B';
        }
    }


    /**
     * Set the numrical value of the number
     * Normalize the number by the format #.###
     * And set the numberString value
     * @param termDoubleValue
     */
    public void setByFormat(double termDoubleValue){
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.HALF_EVEN); //Setting up the formatter
        if ((termDoubleValue != Math.floor(termDoubleValue)))
            this.numberString  = df.format(termDoubleValue).toString();
        else
            this.numberString = (int)termDoubleValue + "";
    }


    @Override
    public String toString() {
        if(isNegative)
            return "-"+numberString;
        else
            return numberString;
    }
}