package Project_Part_One.Document;

public class Document {

    private StringBuilder docNo;
    private StringBuilder date;
    private StringBuilder title;
    private StringBuilder text;

    /**
     * Class Representing a document in the corpus
     * @param docNo String representing the id of the document
     * @param date The Issue date of the document
     * @param title The title of the document
     * @param text The text of the document
     * @throws IllegalArgumentException
     */
    public Document(StringBuilder docNo, StringBuilder date, StringBuilder title, StringBuilder text) throws IllegalArgumentException
    {
        this.docNo = new StringBuilder(docNo);
        this.date = new StringBuilder(date);
        this.title = new StringBuilder(title);
        this.text = new StringBuilder(text);
    }

    /**
     * Getter for the text of the document
     * @return The text of the document
     */
    public StringBuilder getText()
    {
        return this.text;
    }

    /**
     * Getter for the DocNo of the document
     * @return The docNo of the document
     */
    public StringBuilder getDocNo()
    {
        return this.docNo;
    }

}
