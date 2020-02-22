package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Class representing price term
 */
public class PriceTerm extends Term {

    private String price;

    /**
     * This is the constructor for Price Term
     * The is 3 Main rules, and sub rules.
     * Price1 - for numbers less than Million
     * Price2 - number => Million
     * Price can have % that may be at both of options above, for priceTerm with %
     * Price can have dollars/Dollars
     * @param priceTerm String representing price term
     * @param doc Project_Part_One.Document containing the term
     * @throws IllegalArgumentException
     */
    public PriceTerm(String priceTerm, Document doc) throws IllegalArgumentException {
        super(doc);
        priceTerm = priceTerm.replaceAll(",","");
        priceTerm = priceTerm.toLowerCase();
        String[] priceSplit = priceTerm.split("[\\s-]");
        if(priceTerm.charAt(0) == '$')
        {
            if(priceSplit.length == 2) {
                priceSplit[0] = priceSplit[0].substring(1);
                setPriceWithMillionBillionTrillion(priceSplit);
            }
            else if(priceTerm.contains("m") || priceTerm.contains("bn"))
            {
                setPriceByRuleBnM(priceTerm.substring(1));
            }
            else
                setPriceByOnlyNumber(priceTerm.substring(1));
        }
        else
        {
            if(priceSplit.length == 2 && !(priceSplit[0].contains("m") || priceSplit[0].contains("bn")))
                setPriceByOnlyNumber(priceSplit[0]);
            else if(priceSplit[1].contains("/"))
                this.price = priceSplit[0] + " " + priceSplit[1] + " Dollars";
            else if(priceSplit[0].contains("m") || priceSplit[0].contains("bn"))
                setPriceByRuleBnM(priceSplit[0]);
            else if(priceSplit[1].contains("illion"))
                setPriceWithMillionBillionTrillion(priceSplit);
            else
                setPriceByOnlyNumber(priceSplit[0]);
        }
    }


    /**
     * Price2 , more than million
     * Set and Normalize terms of type: $100 billion - > 100000 M Dollars ,$100 million -> 100 M Dollars
     * @param priceSplit
     */
    private void setPriceWithMillionBillionTrillion(String[] priceSplit){
        if(priceSplit[0].charAt(0) == '$')
            priceSplit[0] = priceSplit[0].substring(1);
        if (priceSplit[1].contains("million")) //Price2.iii of type $100 million
            this.price = priceSplit[0] + " M Dollars";
        else if(priceSplit[1].contains("billion")) //Price2.iv of type  $100 billion
        {
            double priceNum = Double.parseDouble(priceSplit[0]) * 1000;
            if(Math.floor(priceNum) == priceNum)
                this.price = (int)priceNum + " M Dollars";
            else
                this.price =  priceNum + " M Dollars";
        }
        else
        {
            double priceNum = Double.parseDouble(priceSplit[0]) * 1000000;
            if(Math.floor(priceNum) == priceNum)
                this.price = (int)priceNum + " M Dollars";
            else
                this.price =  priceNum + " M Dollars";
        }
    }


    /**
     * Set and Normalize Price Term for Price 2.v 100m & Price2.vi of type 100bn
     * @param tempString
     */
    public void setPriceByRuleBnM(String tempString){
        String neededExp = null;

        if (tempString.contains("bn")) { //of type 100bn Dollars
            neededExp = tempString.substring(0, tempString.length() - 2);//Normalization for bn
            this.price = neededExp + "000 M Dollars";
        } else {// of type 20.6m Dollars
            neededExp = tempString.substring(0, tempString.length() - 1);//Normalization for m
            this.price = neededExp + " M Dollars";
        }
    }


    /**
     * Transforming the term into unified pattern
     * @param tempString term to unify
     */
    public void setPriceByOnlyNumber(String tempString){
        // Round to 3 numbers after dot
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        double valuePrice =  Double.parseDouble(tempString);
        //Rule Price1.i
        if (valuePrice<1000000){
            if ((valuePrice == Math.floor(valuePrice))) {
                this.price =  ((int)valuePrice) + " Dollars";
            }
            else{
                this.price = df.format(valuePrice) + " Dollars"; //Round it up
            }
        }
        //Rule Price2.i larger the 1M
        else{
            valuePrice = valuePrice/1000000;
            if ((valuePrice == Math.floor(valuePrice))) {
                this.price =  ((int)valuePrice) + " M Dollars";
            }
            else{
                this.price = df.format(valuePrice) + " M Dollars"; //Round it up
            }
        }
    }


    /**
     * This function return the PriceTerm by format -> example:  50 Dollars
     * @return
     */
    @Override
    public String toString() {
        return price;
    }
}
