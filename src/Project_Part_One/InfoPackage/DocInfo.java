package Project_Part_One.InfoPackage;

import Project_Part_One.ComperatorPackage.PairIntegerBackwardsComparator;
import javafx.util.Pair;

import java.util.PriorityQueue;

/**
 * Class for containing information about document
 */
public class DocInfo {


    private int numOfTermsInDoc;
    private int maxTF;
    private int docVectorLength;
    private PriorityQueue<Pair<String, Integer>> topEntities;
    private String docID;
    private String top;
    private String rank;

    static double avgdl = 0;


    /**
     * Constructor for DocInfo class
     */
    public DocInfo()
    {
        this.numOfTermsInDoc = 0;
        this.maxTF = 0;
        this.docVectorLength = 0;
        this.topEntities = new PriorityQueue<>(new PairIntegerBackwardsComparator());
        this.docID ="";
        this.top = "";
        this.rank ="";
    }

    public DocInfo(int numOfTermsInDoc, int maxTF, int docVectorLength) {
        this.numOfTermsInDoc = numOfTermsInDoc;
        this.maxTF = maxTF;
        this.docVectorLength = docVectorLength;
    }

    /**
     * Add appearance of certain term number of times in the document
     * @param numOfSpecificTermInDoc The number of time the term appeared in the document
     */
    public void addTermsToDoc(int numOfSpecificTermInDoc) {
        this.numOfTermsInDoc += numOfSpecificTermInDoc;
        this.maxTF = Math.max(maxTF, numOfSpecificTermInDoc);
        this.docVectorLength += Math.pow(numOfSpecificTermInDoc,2);
    }


    /**
     * getter for the number of terms in the document
     * @return The number of unique words in the document
     */
    public int getNumOfTermsInDoc() {
        return numOfTermsInDoc;
    }


    /**
     * Getter for the number of time the most common word appeared in the document
     * @return
     */
    public int getMaxTF() {
        return maxTF;
    }


    /**
     * Getter for the result of the next formula: Sum[(frequency of each word in the document)^2]
     * @return
     */
    public int getDocVectorLength() {
        return docVectorLength;
    }

    synchronized public void addEntityToDoc(String entity, int numOfOccurrences)
    {
        int entityWeight = sumEntityWeight(entity, numOfOccurrences);
        if(topEntities.size() < 5)
            this.topEntities.add(new Pair<>(entity, entityWeight));
        else
        {
            if(entityWeight > this.topEntities.peek().getValue())
            {
                this.topEntities.poll();
                this.topEntities.add(new Pair<>(entity, numOfOccurrences));
            }
        }
    }

    private int sumEntityWeight(String entity, int numOfOccurrences) {
        return numOfOccurrences;
    }

    public PriorityQueue<Pair<String, Integer>> getTopEntities() {
        return new PriorityQueue<>(topEntities);
    }

    public void setTopEntities(PriorityQueue<Pair<String, Integer>> topEntities) {
        this.topEntities = topEntities;
    }

    public static void setAvgdl(double averageLength)
    {
        avgdl = averageLength;
    }

    public static double getAvgdl() {
        return avgdl;
    }

    public void setDocNum(String docNum) {
        this.docID = docNum;
    }

    public String getDocID() {
        return docID;
    }

    /**
     * Setting the top 5 string
     */
    public void setTopFiveEntities(){
        this.top = "";
        PriorityQueue<Pair<String, Integer>> tempPQ = new PriorityQueue<>(new PairIntegerBackwardsComparator());
        int size = topEntities.size();
        for (int i=0 ; i<size; i++)
        {
            Pair<String, Integer> pair = topEntities.poll();
            this.top = pair.getKey() + " Rank:"+pair.getValue()+", "+this.top;
            tempPQ.add(pair);
        }
        this.topEntities = tempPQ;
    }

    /**
     * Getter
     * @return
     */
    public String getTop() { return top;
    }


}
