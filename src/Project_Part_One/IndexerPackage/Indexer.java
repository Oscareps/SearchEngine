package Project_Part_One.IndexerPackage;

import Project_Part_One.InfoPackage.DocInfo;
import Project_Part_One.InfoPackage.TermInfo;
import sun.awt.Mutex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * Indexer class extending the Thread Class. The class reads the segment files from the second storage
 * and creating from them posting files, document dictionary and term dictionary
 */
public class Indexer extends Thread implements IndexerInterface{

    private String path;
    private Hashtable<String, TermInfo> termDictionary;
    private Hashtable<String, DocInfo> docDictionary;

    private int startFile;
    private int endFile;
    private int wordsPerPostingFile;


    /**
     * Constructor for the Indexer Class
     * @param path The path of the Temp folder containing the segment
     * @param termDictionary Pointer for the HashMap representing the term dictionary to update
     * @param docDictionary Pointer for the HashMap representing the document dictionary to update
     * @param startFile The starting index of the segment file this indexer thread need to read and handle
     * @param endFile The ending index of the segment file this indexer thread need to read and handle
     * @param wordsPerPostingFile How many words per posting files
     * @throws IllegalArgumentException
     */
    public Indexer(String path, Hashtable<String, TermInfo> termDictionary, Hashtable<String, DocInfo> docDictionary, int startFile, int endFile, int wordsPerPostingFile) throws IllegalArgumentException
    {
        if(termDictionary == null)
            throw new IllegalArgumentException("Dictionary given is illegal");

        this.path = path;
        this.termDictionary = termDictionary;
        this.docDictionary = docDictionary;
        this.startFile = startFile;
        this.endFile = endFile;
        this.wordsPerPostingFile = wordsPerPostingFile;
    }


    @Override
    public void run() {
        indexFiles();
    }


    /**
     * Indexes the files given in the constructor
     */
    public void indexFiles()
    {
        for(int i=startFile ; i<endFile ; i++)
        {
            String filePath = path + "\\Temp\\" + i;
            indexFile(filePath, i);
            try {
                Files.delete(Paths.get(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Index given file and combine all same terms inside it to the same hashtable
     * @param filePath The path of the file to index
     * @param fileNum The name of the file to index
     */
    private void indexFile(String filePath, int fileNum) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
            HashMap<String, List<String>> segmentDict = new HashMap<>();
            String termLine;
            while((termLine = reader.readLine()) != null)
            {
                String[] term_doc_occurrencesInDoc = termLine.split(":");
                if(term_doc_occurrencesInDoc.length < 2)
                {
                    System.out.println(termLine);
                    System.out.println(fileNum);
                }
                String termString = term_doc_occurrencesInDoc[0], docDetails = term_doc_occurrencesInDoc[1];

                if(!segmentDict.containsKey(termString))
                {
                    if(segmentDict.containsKey(termString.toUpperCase())) // If encountered this term only with capital letter before and now with lower letter
                    {
                        List<String> temp = segmentDict.remove(termString.toUpperCase());
                        segmentDict.put(termString, temp);
                    }
                    else if(segmentDict.containsKey(termString.toLowerCase())) // If encountered this term with lower letter before and now with capital letter
                        termString = termString.toLowerCase();
                    else
                        segmentDict.put(termString, new ArrayList<>());
                }
                segmentDict.get(termString).add(docDetails);
            }

            reader.close();

            indexHashMap(segmentDict, fileNum);

            }catch (IOException e) { e.printStackTrace(); }
    }


    /**
     * Writing the segment HashTable into posting files, updating term dictionary and document dictionary on the way
     * @param segmentDict
     * @param fileNum
     */
    private void indexHashMap(HashMap<String, List<String>> segmentDict, int fileNum) {
        int postingFileNum = 0;
        int numOfWordsCount = 0;
        int bytesLocationCount = 0;
        try {
            StringBuilder postingFile = new StringBuilder();

            for (Map.Entry<String, List<String>> entry : segmentDict.entrySet()) {
                if (numOfWordsCount >= wordsPerPostingFile) {
                    Files.write(Paths.get(path + "\\Posting Files\\PostingFile" + fileNum + "_" + postingFileNum), postingFile.toString().getBytes());
                    postingFileNum++;
                    numOfWordsCount = 0;
                    bytesLocationCount = 0;
                    postingFile = new StringBuilder();
                }

                String term = entry.getKey();
                TermInfo termInfo = new TermInfo();

                StringBuilder docDetails = postingToString(entry.getValue(), termInfo);

                //<editor-fold desc="Checking for entities">
                if(term.charAt(0) == '!')
                    if(checkIfEntityAppearedOnce(entry.getValue()))
                        continue;
                    else {
                        term = term.substring(1);
                        for(String doc : entry.getValue())
                        {
                            String[] doc_occurrencesInDoc = doc.split(",");
                            docDictionary.get(doc_occurrencesInDoc[0]).addEntityToDoc(term, Integer.parseInt(doc_occurrencesInDoc[1]));
                        }

                    }
                //</editor-fold>

                termDictionary.put(term, termInfo);

                String postingFileEntry = term + "|" + termDictionary.get(term).getNumOfDocsIn() + ":" + docDetails + "\n";

                postingFile.append(postingFileEntry);

                termDictionary.get(term).setLocation(fileNum + "_" + postingFileNum + "," + bytesLocationCount);
                bytesLocationCount += postingFileEntry.getBytes().length;
                numOfWordsCount++;
            }

            Files.write(Paths.get(path + "\\Posting Files\\PostingFile" + fileNum + "_" + postingFileNum), postingFile.toString().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Transforming the list of the docs given to one string, collecting data on the concatenated documents on the way
     * @param postingForWord
     * @param termInfo
     * @return
     */
    private StringBuilder postingToString(List<String> postingForWord, TermInfo termInfo) {
        StringBuilder res = new StringBuilder();

        for(String docDetails : postingForWord)
        {
            String[] doc_occurrencesInDoc = docDetails.split(",");

            if(!docDictionary.containsKey(doc_occurrencesInDoc[0]))
                this.docDictionary.put(doc_occurrencesInDoc[0], new DocInfo());
            this.docDictionary.get(doc_occurrencesInDoc[0]).addTermsToDoc(Integer.parseInt(doc_occurrencesInDoc[1]));

            termInfo.addOccurenceOfTerm(Integer.parseInt(doc_occurrencesInDoc[1]));


            res.append(docDetails + "#");
        }

        return res;
    }


    /**
     * Checks if the given entity appeared more then one time in all of the corpus
     * @param entityPosting All of the documents the entity appeared in
     * @return true if the entity appeared only one time in the corpus, false otherwise
     */
    private boolean checkIfEntityAppearedOnce(List<String> entityPosting) {
        if(entityPosting.size() > 2)
            return false;
        String[] doc_occurrencesInDoc = entityPosting.get(0).split(",");
        if(doc_occurrencesInDoc[1].equals("1"))
            return true;
        return false;
    }
}
