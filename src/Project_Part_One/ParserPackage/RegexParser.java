package Project_Part_One.ParserPackage;

import Project_Part_One.Document.Document;
import Project_Part_One.ExceptionPackage.IllegalWordException;
import Project_Part_One.ExceptionPackage.StopWordException;
import Project_Part_One.ReadFilePackage.IReadFile;
import Project_Part_One.TermsPackage.*;
import Project_Part_Two.Query;
import Project_Part_Two.Word2Vec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegexParser implements the IParser interface. This class uses only Regular Expressions to parse the data, and create from it
 * different types of terms. For each document the parser collects all of the unique terms and write it into Segment files in the Disk.
 * The RegexParser class extends the Thread class.
 */
public class RegexParser extends Thread implements IParser {

    //<editor-fold desc="Regex">
    //<editor-fold desc="Commonly used strings for regex">
    final private String NUM_NO_COMMA_REGEX = "(?<![\\d\\w])([1-9](\\d)*(\\.[0-9]+)?)";
    final private String FRACTION_REGEX = "(([1-9][0-9]*)/([1-9][0-9]*))";
    final private String NUM_REGEX = "(?<![\\d\\w])[1-9](([0-9]{0,2},([0-9]{3})+)|(\\d*))((\\.[0-9]+)|(\\s"+FRACTION_REGEX+"))?";
    final private String M_BN_TR_REGEX = "(m|(bn)|([\\s-](m|b|tr)illion))\\b";
    //</editor-fold>

    //<editor-fold desc="Strings for price regex">
    final private String PRICE_SIGNED_REGEX = "(\\$"+ NUM_REGEX +"("+M_BN_TR_REGEX +")?)";
    final private String PRICE_UNSIGNED_REGEX = NUM_REGEX +"((\\s"+ FRACTION_REGEX +")|"+"("+ M_BN_TR_REGEX +"))?(\\sU.S.)?( dollars\\b)";
    //</editor-fold>

    //<editor-fold desc="Strings for date regex">
    final private String DATE_MONTH_REGEX;

    {
        DATE_MONTH_REGEX = "\\b((jan(uary)?)|(feb(ruery)?)|(mar(ch)?)|(apr(il)?)|(may)|(jun(e)?)|(jul(y)?)|(aug(ust)?)|(sep(tember)?)|(oct(ober)?)|(nov(ember)?)|(dec(ember)?))\\b";
    }

    final private String DATE_DAY_REGEX = "(?<![\\d\\w])(([12][1-9])|(3[01])|(0?[1-9]))(?![\\d\\w])";
    final private String DATE_YEAR_REGEX = "(?<![\\d\\w])([1-9]\\d{3})(?![\\d\\w])";
    final private String DATE_DAY_MONTH_REGEX = "(("+ DATE_DAY_REGEX +"\\s"+ DATE_MONTH_REGEX +")|("+ DATE_MONTH_REGEX +"\\s"+ DATE_DAY_REGEX +"))";
    final private String DATE_YEAR_MONTH_REGEX = "(("+ DATE_MONTH_REGEX +"\\s"+ DATE_YEAR_REGEX +"))";
    //</editor-fold>

    //<editor-fold desc="Strings for word-word or word-num regex">
    final private String HYPHEN_WORD_TO_WORD_STRING = "(\\b([a-zA-Z]+)(-[a-zA-Z]+)+\\b)";
    final private String HYPHEN_NUM_TO_WORD_STRING = "(((?<!\\w)"+ NUM_NO_COMMA_REGEX +"-([a-zA-Z]+)\\b)|(\\b([a-zA-Z]+)-"+ NUM_NO_COMMA_REGEX +"(?!\\w)))";
    //</editor-fold>

    //<editor-fold desc="Strings for num-num regex">
    final private String NUM_RANGE_HYPHEN_STRING = "((?<!\\w)"+ NUM_NO_COMMA_REGEX +"-"+ NUM_NO_COMMA_REGEX +"(?!\\w))";
    final private String NUM_RANGE_BETWEEN_STRING = "(\\b(between "+ NUM_NO_COMMA_REGEX +" and "+ NUM_NO_COMMA_REGEX +")(?!\\w))";
    //</editor-fold>

    //<editor-fold desc="Patterns">
    final private Pattern PERCENT_PATTERN = Pattern.compile("(("+ NUM_NO_COMMA_REGEX +"%)|("+ NUM_NO_COMMA_REGEX +" percent(age)?))", Pattern.CASE_INSENSITIVE);
    final private Pattern PRICE_PATTERN = Pattern.compile(PRICE_SIGNED_REGEX + "|"+ PRICE_UNSIGNED_REGEX, Pattern.CASE_INSENSITIVE);
    final private Pattern DATE_PATTERN = Pattern.compile(DATE_YEAR_MONTH_REGEX +"|"+ DATE_DAY_MONTH_REGEX,Pattern.CASE_INSENSITIVE);
    final private Pattern HYPHEN_WORD_PATTERN = Pattern.compile(HYPHEN_WORD_TO_WORD_STRING +"|"+ HYPHEN_NUM_TO_WORD_STRING);
    final private Pattern NUM_RANGE_PATTERN = Pattern.compile(NUM_RANGE_HYPHEN_STRING +"|"+ NUM_RANGE_BETWEEN_STRING);
    final private Pattern ENTITY_PATTERN = Pattern.compile("(?<![\\w\\d])([A-Z][a-z]+)( [A-Z][a-z]+)+(?![\\d])");
//    final private Pattern ENTITY_PATTERN = Pattern.compile("(?<![\\w\\d])([A-Z]+[a-z]*)( [A-Z]+[a-z]*)+(?![\\d])");
    final private Pattern PHONE_PATTERN = Pattern.compile("(\\(\\d{3}\\) \\d{3}-?\\d{4})");
    final private Pattern PARENTHESIS_PATTERN = Pattern.compile("\\([\\da-zA-Z]+\\)");
    final private Pattern NUMBER_PATTERN = Pattern.compile("(-)?("+ NUM_REGEX +")("+ M_BN_TR_REGEX +")?");
    final private Pattern WORD_PATTERN = Pattern.compile("(?<![\\w\\d])([a-z][a-z]+)|([A-Z](((\\.[A-Z])+)|([A-Za-z]+)))(?![\\w\\d])");

    final private Pattern[] patternArr = {PERCENT_PATTERN, PRICE_PATTERN, DATE_PATTERN, HYPHEN_WORD_PATTERN, PHONE_PATTERN, PARENTHESIS_PATTERN, NUM_RANGE_PATTERN, ENTITY_PATTERN, NUMBER_PATTERN, WORD_PATTERN};
    //</editor-fold>
    //</editor-fold>

    private IReadFile readFile;
    private HashMap<Integer, List<Term>> segmentMap;
    private int docBatch;
    private int numOfTempFiles;
    private String tempPath;
    static private Semaphore[] lockArr = new Semaphore[50];
    static private boolean isStem;


    /**
     * Constructor for the RegexParser class
     * @param readFile Pointer to ReadFile class
     * @param docBatch How many documents to parse at each batch
     * @param tempPath The path to write the segments files into
     * @param numOfTempFiles The number of segments files
     * @throws IllegalArgumentException
     */
    public RegexParser(IReadFile readFile, int docBatch, String tempPath, int numOfTempFiles) throws IllegalArgumentException {
        if(readFile == null)
            throw new IllegalArgumentException("IReadFile argument in Parser is not valid");
        if(docBatch < 0)
            throw new IllegalArgumentException("docBatch argument in Parser is not valid");

        this.readFile = readFile;
        this.segmentMap = new HashMap<>();
        this.docBatch = docBatch;
        this.isStem = false;
        this.tempPath = tempPath + "\\Temp";
        this.numOfTempFiles = numOfTempFiles;

    }

    public RegexParser()
    {
        this.isStem = false;
    }



    @Override
    public void run() {
        parseDocs();
    }

    @Override
    public void parseDocs() {

        List<Document> docList = readFile.getDocs(docBatch);
        while(docList.size() != 0)
        {
            parseDocBatch(docList);
            writeToSegmentFile();
            segmentMap = new HashMap<>();
            docList = readFile.getDocs(docBatch);
        }
    }


    /**
     * Write the segment HashMap into the segment files in the Disk
     */
    private void writeToSegmentFile() {
        try {
            for (Map.Entry<Integer, List<Term>> entry : segmentMap.entrySet()) {

                lockArr[entry.getKey()].acquire();
                if(Files.notExists(Paths.get(tempPath + "\\" + entry.getKey())))
                    Files.createFile(Paths.get(tempPath + "\\" + entry.getKey()));
                StringBuilder appendToFile = new StringBuilder();
                for(Term term : entry.getValue())
                {
                    appendToFile.append(term.toString());
                    appendToFile.append(":");
                    appendToFile.append(term.getDocNo() + "," + term.getNumOfOccurrences() + "\n");
                }

                Files.write(Paths.get(tempPath + "\\" + entry.getKey()), appendToFile.toString().getBytes(), StandardOpenOption.APPEND);
                lockArr[entry.getKey()].release();

            }
        }catch(IOException e) { e.printStackTrace(); } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    /**
     * Parsing batch of documents
     * @param docList The documents to parse
     */
    public void parseDocBatch(List<Document> docList) {

        for(Document doc : docList)
        {
            HashMap<String,Term> termMap = new HashMap<>();

            runRegexPatterns(doc, termMap);

            addToSegmentMap(termMap);
        }

    }


    public HashMap<String, Term> parseQuery(Query query,boolean isStem,boolean isSemantic)
    {
        String queryContent  = query.getQueryTitle();
        String[] splitedQuery =  queryContent.split(" ");
        List<String> queryToList = Arrays.asList(splitedQuery);
        String semanticStr = "";
        if(isSemantic){
            Word2Vec word2Vec = new Word2Vec(queryToList);
            semanticStr =  word2Vec.getQueryAfterSematicAdd();
        }
        Document queryDoc = new Document(new StringBuilder(query.getQueryID()), new StringBuilder(), new StringBuilder(),
//                new StringBuilder(queryContent+" "+ semanticStr+ " " + queryContent.toLowerCase() + query.getQueryDesc() ));
                new StringBuilder(queryContent+" "+ semanticStr+ " " + queryContent.toLowerCase().replaceAll("-" ,"") + query.getQueryDesc() ));
        HashMap<String, Term> queryTerms = new HashMap<>();
        runRegexPatterns(queryDoc, queryTerms);
        return queryTerms;
    }


    /**
     * Runs all of the regex patterns on given text
     * @param doc The document wich the text is in
     * @param termMap The map to hold the terms caught
     */
    private void runRegexPatterns(Document doc, HashMap<String, Term> termMap)
    {
        StringBuilder text = doc.getText();

        for(int i=0; i<patternArr.length; i++) { // run all patterns on text

            Matcher m = patternArr[i].matcher(text);
            List<int[]> toDel = new ArrayList<>(); // List of positions of terms to delete from text

            while (m.find()) {
                String termCaught = m.group();
                Term newTerm = termFactory(i, termCaught, doc);
                if(newTerm != null) {
                    addToMap(newTerm, termMap);
                }
                int termToDelPos[] = {m.start(), m.end()};
                toDel.add(termToDelPos);
            }

            for (int j = toDel.size()-1; j>=0 && i < patternArr.length-2 ; j--) {
                text.delete(toDel.get(j)[0], toDel.get(j)[1]);
            }
        }
    }


    /**
     * Adds all elements in termMap into the SegmentMap
     * @param termMap The termMap whos elements we will add to SegmentMap
     */
    private void addToSegmentMap(HashMap<String, Term> termMap) {
        for(Term term : termMap.values())
        {
            int segmentName = Math.abs(term.toString().toLowerCase().hashCode()%numOfTempFiles);
            if(!segmentMap.containsKey(segmentName))
                segmentMap.put(segmentName, new ArrayList<>());
            segmentMap.get(segmentName).add(term);
        }
    }


    /**
     * Adds the term into the termMap
     * @param newTerm The term to add
     * @param termMap The termMap to add the term into
     */
    private void addToMap(Term newTerm, HashMap<String, Term> termMap)
    {
        Term oldTerm = termMap.get(newTerm.toString());
        if(oldTerm != null)
            oldTerm.addOccurrence();
        else
            termMap.put(newTerm.toString(), newTerm);

    }


    /**
     * Factory Design Pattern that creates certain term type according the given int
     * @param i The indicator who decides which term to create
     * @param term The term we want to create
     * @param doc The document holding the term
     * @return The new term created
     */
    private Term termFactory(int i, String term, Document doc)
    {
        if(i == 0)
            return new PercentTerm(term, doc);
        if(i == 1)
            return new PriceTerm(term, doc);
        if(i == 2)
            return new DateTerm(term, doc);
        if(i == 3)
            return new HyphenWordTerm(term, doc);
        if(i == 4)
            return new PhoneTerm(term, doc);
        if(i == 5)
            return new ParenthesisTerm(term, doc);
        if(i == 6)
            return new NumberRangeTerm(term, doc);
        if(i == 7)
            return new EntitieTerm(term, doc);
        if(i == 8)
            return new NumberTerm(term, doc);
        if(i == 9)
            try {return new WordTerm(term, doc);}
            catch (StopWordException e) {return null;}
            catch (IllegalArgumentException e) {e.printStackTrace();}
            catch (IllegalWordException e) { return null; }
        return null;
    }


    /**
     * Set stemming on or off
     * @param stem Status of the stem
     */
    public static void setStem(boolean stem) {
        isStem = stem;
        WordTerm.setStem(stem);
    }


    /**
     * Initializing the locks on all of the segment files
     */
    public static void initLocks()
    {
        for(int i=0 ; i<lockArr.length ; i++)
            lockArr[i] = new Semaphore(1);
    }
}
