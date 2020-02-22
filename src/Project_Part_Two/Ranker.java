package Project_Part_Two;

import Project_Part_One.ComperatorPackage.PairDoubleComparator;
import Project_Part_One.InfoPackage.DocInfo;
import Project_Part_One.TermsPackage.EntitieTerm;
import Project_Part_One.TermsPackage.Term;
import javafx.util.Pair;


import java.util.*;

/**
 * This class is responisble for calculating the score of each document
 */
public class Ranker {

    Hashtable<String, DocInfo> docDictionary;
    int N;
    private double b; // Free parameter, usually chosen as b = 0.75.
    private double k1; //Free Parameter ,  usually chosen as k1 = 2.0.


    public Ranker(Hashtable<String, DocInfo> docDictionary) {
        this.docDictionary = docDictionary;
        this.N = docDictionary.size();

    }

    /**
     * Ranking function
     * @param termList
     * @return
     */
    public List<Pair<String, Double>> rank(List<TermInfoInCorpus> termList) {
        List<Pair<String, Double>> rankedList = null;
        //Data struct containing all the doc options for the query


        HashMap<String, Pair<DocInfo, List<DocRankingTermInfo>>> docMap = generateMapDic(termList);
        rankedList = rankedDocMap(docMap);
        List<Pair<String, Double>> topFifty = rankTopFifty(rankedList);
        //For each document we will give it a rank
        return topFifty;

    }

    /**
     * Generating docMap HashTable from the given term list
     * @param termList
     * @return
     */
    public HashMap<String, Pair<DocInfo, List<DocRankingTermInfo>>> generateMapDic(List<TermInfoInCorpus> termList) {
        HashMap<String, Pair<DocInfo, List<DocRankingTermInfo>>> docMap = new HashMap<>();
        //For each term in the term list we will iterate and extract all the documents containing those term
        for (TermInfoInCorpus currTerm : termList) {
            List<TermInfoInDoc> docsGotTermList = currTerm.getDocListInfo();
            currTerm.getTermInfo().setNumOfDocsIn(countAsSet(docsGotTermList));
            updateDocHashMap(docMap, docsGotTermList, currTerm);

        }
        return docMap;
    }

    /**
     * Bandage for counting the number of docs containing the term
     * @param docsGotTermList
     * @return
     */
    public int countAsSet(List<TermInfoInDoc> docsGotTermList) {
        Set<String> docSet = new HashSet<>();
        for (TermInfoInDoc termInfoInDoc : docsGotTermList) {
            docSet.add(termInfoInDoc.getDocInfo().getDocID());
        }
        return docSet.size();
    }

    /**
     * Adding all the documents having the term to the doc Map HashTable
     * @param docMap
     * @param docsGotTermList
     * @param currTerm
     */
    public void updateDocHashMap(HashMap<String, Pair<DocInfo, List<DocRankingTermInfo>>> docMap,
                                 List<TermInfoInDoc> docsGotTermList, TermInfoInCorpus currTerm) {
        for (TermInfoInDoc termInfoInDoc : docsGotTermList) {
            //Adding new Document to HashMap
            if (!docMap.containsKey(termInfoInDoc.getDocNo())) {
                List<DocRankingTermInfo> listDocTermInfo = new ArrayList<>();
                Pair<DocInfo, List<DocRankingTermInfo>> pair = new Pair<>(termInfoInDoc.getDocInfo(), listDocTermInfo);
                docMap.put(termInfoInDoc.getDocNo(), pair);
            }
            //Adding new term to term list of the doc
            Pair<DocInfo, List<DocRankingTermInfo>> entryPair = docMap.get(termInfoInDoc.getDocNo());
            entryPair.getValue().add(new DocRankingTermInfo(termInfoInDoc.getDocNo(), currTerm.getTerm(), termInfoInDoc.getTF(), currTerm.getTermInfo()));
//                docMap.put(termInfoInDoc.getDocNo(),entryPair);
        }

    }

    /**
     * For each entry of docMap calculate the value of the ranking per query
     * @param docMap
     * @return
     */
    private List<Pair<String, Double>> rankedDocMap(HashMap<String, Pair<DocInfo, List<DocRankingTermInfo>>> docMap) {
        Iterator it = docMap.entrySet().iterator();
        List<Pair<String, Double>> rankedList = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry docEntry = (Map.Entry) it.next();
            String docID = (String) docEntry.getKey();
            Double docRank = rankDocument((Pair<DocInfo, List<DocRankingTermInfo>>) docEntry.getValue());
            Pair<String, Double> docResultPair = new Pair<>(docID, docRank);
            rankedList.add(docResultPair);
        }
        return rankedList;
    }


    /**
     * Calculte each doc ranking by BM 25 , for more info https://en.wikipedia.org/wiki/Okapi_BM25
     * <p>
     * Math.log(x) / Math.log(2) = log[2](x)
     *
     * @param docInfoPair
     * @return
     */
    public Double rankDocument(Pair<DocInfo, List<DocRankingTermInfo>> docInfoPair) {

        double K1 = 1.12;
        double b = 0.6;
        int docLength = docInfoPair.getKey().getNumOfTermsInDoc();
        double docRanking = 0;
        for (DocRankingTermInfo docTermInfo : docInfoPair.getValue()) {
            int numberOfDocContainingTerm = docTermInfo.getTermInfo().getNumOfDocsIn();
            int dfi = docTermInfo.getTfInDoc();
            if (dfi == 0)
                continue;
            else {
                double value = ((N - numberOfDocContainingTerm + 0.5) / (numberOfDocContainingTerm + 0.5));
                double idf = Math.log(value); /// log{10}(N/IDF)
                double mone = dfi * (K1 + 1);
                double mechane = dfi + K1 * (1 - b + (b * docLength / DocInfo.getAvgdl()));
                docRanking += (idf * (mone / mechane));

            }
        }
        return docRanking;
    }

    /**
     * Return top fifty of the ranked list
     * @param rankedList
     * @return
     */
    private List<Pair<String, Double>> rankTopFifty(List<Pair<String, Double>> rankedList) {

        rankedList.sort(new PairDoubleComparator());

        if (rankedList.size() < 50) {
            return rankedList;
        }

        List<Pair<String, Double>> topFifty = rankedList.subList(0, 50);

        return topFifty;
    }

}
