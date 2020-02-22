package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;
import Project_Part_One.ExceptionPackage.IllegalWordException;
import Project_Part_One.ExceptionPackage.StopWordException;
import StemmerPackage.PorterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Class representing Word
 */
public class WordTerm extends Term {

    private String wordText;
    private boolean isCapital;

    private static HashSet<String> stopWords;

    private static boolean isStem = false;


    /**
     * Constructor for WordTerm class
     * @param wordTerm String representing the word in the document
     * @param doc Project_Part_One.Document holding the word
     * @throws IllegalArgumentException
     * @throws StopWordException
     * @throws IllegalWordException
     */
    public WordTerm(String wordTerm, Document doc) throws IllegalArgumentException, StopWordException, IllegalWordException {
        super(doc);

        if(stopWords.contains(wordTerm.toLowerCase()))
            throw new StopWordException();

        if(wordTerm.charAt(0) <= 'Z' && wordTerm.charAt(0) >= 'A')
            isCapital = true;
        else
            isCapital = false;

        if(isStem)
        {
            String beforeStem = wordTerm;
            PorterStemmer stemmer = new PorterStemmer();
            stemmer.add(wordTerm.toCharArray(),wordTerm.length());
            try {
                stemmer.stem();
                wordTerm = stemmer.toString();
            }
            catch (Exception e) {}

            if(wordTerm.length() < 2)
                throw new IllegalWordException(beforeStem);
        }

        this.wordText = wordTerm;

    }


    /**
     * Function loading the stop words file into memory
     * @param path path to stop words file
     * @return HashSet holding all of the stop words
     */
    private static HashSet<String> getStopWords(String path)
    {
        HashSet<String> stopWordsSet = new HashSet<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            String stopWord;
            while((stopWord=br.readLine()) != null)
            {
                stopWordsSet.add(stopWord);
            }
        } catch(IOException e){e.printStackTrace();}
        return stopWordsSet;
    }


    /**
     * Changing the status of stemming
     * @param stem Status of stemming to change into
     */
    public static void setStem(boolean stem)
    {
        isStem = stem;
    }


    @Override
    public String toString() {
        if(isCapital)
            return wordText.toUpperCase();
        return wordText.toLowerCase();
    }


    /**
     * sets the stop word set
     * @param path The path of the file of the stop words
     */
    public static void setStopWords(String path)
    {
        stopWords = getStopWords(path + "\\stop_words.txt");
    }
}
