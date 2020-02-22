package TestPackage;

import GUIPackage.Controller;
import Project_Part_One.ComperatorPackage.PairIntegerBackwardsComparator;
import Project_Part_One.InfoPackage.DocInfo;
import Project_Part_One.InfoPackage.TermInfo;
import Project_Part_One.ParserPackage.RegexParser;
import Project_Part_One.ReadFilePackage.ReadFile;
import Project_Part_One.TermsPackage.WordTerm;
import Project_Part_Two.Query;
import Project_Part_Two.Searcher;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.PriorityQueue;

@SuppressWarnings("Duplicates")
public class TestPart2 {

    private static Hashtable<String, DocInfo> docDict;
    private static Hashtable<String, TermInfo> termDict;

    public static void main(String[] args) {
        uploadDicToRam();
        WordTerm.setStopWords("Resourcs/stop_words.txt");
        String path = "C:\\Users\\oscarep\\Ret\\IndexFiles\\Output\\Stem Off";
        File indexDirectory = new File(path);
        int indexCount = indexDirectory.list().length - 1;
        Searcher searcher = new Searcher(termDict, docDict, new RegexParser(new ReadFile("C:\\Users\\oscarep\\Ret\\corpus\\corpus"),50,"",2),path+"\\Index" + indexCount);
        Query query = new Query("1", "oil","");
        searcher.search(query,false,false);
    }

    public static void uploadDicToRam() {

        try {
            //<editor-fold desc="Uploading term dictionary">
            File indexDirectory = new File("C:\\Users\\oscarep\\Ret\\IndexFiles\\Output\\Stem Off");

            int indexCount = indexDirectory.list().length - 1;

            File termDictionary = new File("C:\\Users\\oscarep\\Ret\\IndexFiles\\Output\\Stem Off" + "\\Index" + indexCount + "\\Term Dictionary\\dictionary");
            BufferedReader reader = new BufferedReader(new FileReader(termDictionary));
            String line;
            termDict = new Hashtable<>();
            while((line = reader.readLine()) != null)
            {
                String[] lineSplitted = line.split("[:|]");
                termDict.put(lineSplitted[0], new TermInfo(Integer.parseInt(lineSplitted[1]), lineSplitted[2]));
            }
            //</editor-fold>

            //<editor-fold desc="Uploading document dictionary">
            File docDictionary = new File("C:\\Users\\oscarep\\Ret\\IndexFiles\\Output\\Stem Off" + "\\Index" + indexCount + "\\Document Dictionary\\dictionary");
            reader = new BufferedReader(new FileReader(docDictionary));
            docDict = new Hashtable<>();
            while((line = reader.readLine()) != null)
            {
                if(line.contains(":")) {
                    String[] lineSplitted = line.split("[:#]");
                    DocInfo newDoc = new DocInfo(Integer.parseInt(lineSplitted[1]), Integer.parseInt(lineSplitted[2]), Integer.parseInt(lineSplitted[3]));
                    newDoc.setTopEntities(stringToTopEntities(lineSplitted[4]));
                    docDict.put(lineSplitted[0], newDoc);

                }
                else
                    DocInfo.setAvgdl(Double.parseDouble(line));
            }
            //</editor-fold>

        } catch(IOException e)
        {
        }

    }

    private static PriorityQueue<Pair<String, Integer>> stringToTopEntities(String topEntitiesString) {
        PriorityQueue<Pair<String , Integer>> res = new PriorityQueue<>(5, new PairIntegerBackwardsComparator());
        String[] entitiesPairs = topEntitiesString.split(";");
        for(int i=0 ; i<entitiesPairs.length ; i++)
        {
            String[] entity_weight = entitiesPairs[i].split(",");
            res.add(new Pair<>(entity_weight[0], Integer.parseInt(entity_weight[1])));
        }
        return res;
    }
}
