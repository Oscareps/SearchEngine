package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;

public class DateTerm extends Term {

    private String day;
    private String month;
    private String year;

    /**
     * Constructor of date object
     * @param doc the doc wich holds this term
     * @throws IllegalArgumentException
     */
    public DateTerm(String dateTerm, Document doc) throws IllegalArgumentException
    {
        super(doc);
        if(doc == null)
            throw new IllegalArgumentException("Doc argument in term " + dateTerm + "is not valid");
        String[] dateSplit = dateTerm.split("\\s");
        if(dateSplit[0].charAt(0) <= '9' && dateSplit[0].charAt(0) >= '0') // If the first string is a number -> day-month date
        {
            this.day = dateSplit[0];
            this.month = getMonthNum(dateSplit[1]);
        }
        else
        {
            this.month = getMonthNum(dateSplit[0]);
            if(dateSplit[1].length() == 2)
                this.day = dateSplit[1];
            else
                this.year = dateSplit[1];
        }
        if(this.month == null)
            throw new IllegalArgumentException(dateTerm + " is not in valid date pattern");
    }

    /**
     * returns a String with a numeric representation of the month
     * @param str1 String with a num name
     * @return String with numeric representation of the month, null if str is not a month name
     */
    private String getMonthNum(String str1) {
        str1 = str1.toLowerCase();
        if(str1.contains("ja"))
            return "01";
        if(str1.contains("fe"))
            return "02";
        if(str1.contains("mar"))
            return "03";
        if(str1.contains("ap"))
            return "04";
        if(str1.contains("may"))
            return "05";
        if(str1.contains("jun"))
            return "06";
        if(str1.contains("jul"))
            return "07";
        if(str1.contains("au"))
            return "08";
        if(str1.contains("s"))
            return "09";
        if(str1.contains("oc"))
            return "10";
        if(str1.contains("n"))
            return "11";
        if(str1.contains("d"))
            return "12";
        return null;
    }

    @Override
    public String toString() {
        if(year == null)
            return month + "-" + day;
        else
            return year + "-" + month;
    }
}
