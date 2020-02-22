package Project_Part_One.InfoPackage;

/**
 * Class for holding information about term
 */
public class TermInfo {

    private int frequencyInCorpus;
    private int numOfDocsIn;
    private String fileNameLocation;
    private int locationInFile;


    /**
     * Default constructor for the TermInfo class
     */
    public TermInfo()
    {
        this.frequencyInCorpus = 0;
        numOfDocsIn = 0;
        fileNameLocation = "";
        locationInFile = 0;
    }


    /**
     * Constructor for the TermInfo Class
     * @param frequencyInCorpus the frequency of the term in all of the corpus
     * @param location The location of the term in the posting file
     */
    public TermInfo(int frequencyInCorpus, String location) {
        this.frequencyInCorpus = frequencyInCorpus;
        setLocation(location);
    }


    /**
     * Add occurence of the term in some document in the corpus
     * @param numOfTimesInDoc The number of times the term appeared in the document
     */
    public void addOccurenceOfTerm(int numOfTimesInDoc)
    {
        this.frequencyInCorpus += numOfTimesInDoc;
        this.numOfDocsIn++;
    }


    /**
     * Setter for the location of the term in the posting files
     * @param location
     */
    public void setLocation(String location)
    {
        String[] locationDetails = location.split(",");
        this.fileNameLocation = locationDetails[0];
        this.locationInFile = Integer.parseInt(locationDetails[1]);
    }


    public String getFileNameLocation() {
        return fileNameLocation;
    }

    public int getLocationInFile() {
        return locationInFile;
    }

    /**
     * Getter for the frequency of the term in all of the corpus
     * @return
     */
    public int getFrequencyInCorpus()
    {
        return frequencyInCorpus;
    }


    /**
     * Getter for the number of documents the term appeared in
     * @return The number of documents the term appeared in
     */
    public int getNumOfDocsIn()
    {
        return numOfDocsIn;
    }


    /**
     * Combine the data of 2 TermInfo classes
     * @param termInfo The TermInfo to combine with
     */
    public void addNewTermInfo(TermInfo termInfo)
    {
        this.frequencyInCorpus += termInfo.frequencyInCorpus;
        this.numOfDocsIn += termInfo.numOfDocsIn;
    }

    public void setNumOfDocsIn(int numOfDocsIn) {
        this.numOfDocsIn = numOfDocsIn;
    }
}
