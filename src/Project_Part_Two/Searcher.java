package Project_Part_Two;

import Project_Part_One.InfoPackage.DocInfo;
import Project_Part_One.InfoPackage.TermInfo;
import Project_Part_One.ParserPackage.IParser;
import Project_Part_One.TermsPackage.Term;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

/**
 * Searcher responsible for the all searching process
 */
public class Searcher {

    private Hashtable<String, TermInfo> termDict;
    private Hashtable<String, DocInfo> docDict;
    private IParser parser;
    private Ranker ranker;
    private String indexPath;

    public Searcher(Hashtable<String, TermInfo> termDict, Hashtable<String, DocInfo> docDict, IParser parser, String indexerPath) {
        this.termDict = termDict;
        this.docDict = docDict;
        this.parser = parser;
        this.indexPath = indexerPath;
        ranker = new Ranker(docDict);
    }

    /**
     * Search General function
     * @param query
     * @param isStem
     * @param isSemantic
     * @return
     */
    public List<Pair<String, Double>> search(Query query, boolean isStem, boolean isSemantic) {
        List<TermInfoInCorpus> termsInfoInCorpus = getInfoOnQueryTerms(query,isStem,isSemantic);
        List<Pair<String, Double>> docResults = ranker.rank(termsInfoInCorpus);
        return docResults;
    }

    /**
     * Returning list of the {@link TermInfoInDoc} which has all the data needed to calculate the BM25
     * @param query
     * @param isStem
     * @param isSemantic
     * @return
     */
    private List<TermInfoInCorpus> getInfoOnQueryTerms(Query query, boolean isStem, boolean isSemantic) {
        RandomAccessFile reader;

        HashMap<String, Term> queryTerms = parser.parseQuery(query,isStem,isSemantic);

        List<TermInfoInCorpus> termsDocuments = new ArrayList<>();

        for (Term term : queryTerms.values()) {
            String valueWithEntities =  term.toString().replaceAll("!",""); //Might be entitie the way its being saved
            TermInfo termInfo = termDict.get(valueWithEntities);
            // If the term is not in the corpus
            if (termInfo == null) {
                continue;
            }

            String termPosting = "";

            try {
                reader = new RandomAccessFile(new File(indexPath + "\\Posting Files\\PostingFile" + termInfo.getFileNameLocation()), "r");
                reader.seek(termInfo.getLocationInFile());
                termPosting = reader.readLine();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<TermInfoInDoc> docInfoList = getDocInfoFromPosting(termPosting);

            termsDocuments.add(new TermInfoInCorpus(term, termInfo, docInfoList));
        }

        return termsDocuments;
    }

    /**
     * Return list of TermInfoInDoc
     * @param termPosting
     * @return
     */
    private List<TermInfoInDoc> getDocInfoFromPosting(String termPosting) {
        List<TermInfoInDoc> res = new ArrayList<>();
        String[] termDocsInfo = termPosting.split(":")[1].split("#");
        for (String docInfo : termDocsInfo) {
            String[] docInfoElements = docInfo.split(",");
            res.add(new TermInfoInDoc(docInfoElements[0], Integer.parseInt(docInfoElements[1]), docDict.get(docInfoElements[0])));
        }
        return res;
    }

    /**
     * Responsible for extracting all queries from the file
     * @param pathToQueryFile
     * @return
     */
    public List<Query> generateQueriesListFromFile(String pathToQueryFile) {
        List<Query> qureyList = new ArrayList<>();
        BufferedReader bufferedReader;
        File queryFile = new File(pathToQueryFile);
        String line;
        try {
            bufferedReader = new BufferedReader(new FileReader(queryFile));
            while((line = bufferedReader.readLine()) !=null ){
                if(line.contains("<top>")){
                    Query query = generateQuery(line,bufferedReader);
                    qureyList.add(query);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return qureyList;
    }

    /**
     * Generating query out the given query file
     * @param line
     * @param bufferedReader
     * @return
     * @throws IOException
     */
    private Query generateQuery(String line, BufferedReader bufferedReader) throws IOException {
        String num = null;
        String title = null;
        String desc = null;

        while (!line.contains("</top>")
        ) {
            if(line.contains("<num>")){
                num =line;
                num = num.replace("<num> Number: ","");
                num = num.replaceAll(" ","");
            }
            else if(line.contains("<title>")){
                title = line;
                title = title.replace("<title> ","");
            }
            else if(line.contains("<desc>")){
                desc = line;
                desc = desc.replace("<desc> Description: ","");
                line = bufferedReader.readLine();
                while(!line.contains("<narr> Narrative:")){
                    desc = desc +" "+line;
                    line = bufferedReader.readLine();
                }
            }
            line = bufferedReader.readLine();
        }
        return new Query(num,title ,desc);
    }
}
