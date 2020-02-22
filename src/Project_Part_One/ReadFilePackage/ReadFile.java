package Project_Part_One.ReadFilePackage;

import Project_Part_One.Document.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ReadFile implements IReadFile {

    private File rootFolder;
    private BufferedReader bufferedReader;
    private File[] fileArray;
    private int fileIndex;
    private int fileArrayLen;
    private String line;
    private File currentFile;

    private int numOfIndexedDocs;


    public ReadFile(String pathToCorpus) {
        this.rootFolder = new File(pathToCorpus);
        this.fileArray = rootFolder.listFiles();
        this.fileIndex = 1;
        this.fileArrayLen = fileArray.length;
        this.numOfIndexedDocs = 0;
        this.currentFile = new File(fileArray[0] + "\\" + fileArray[0].getName());
        // setting up the buffer
        try {
            this.bufferedReader = new BufferedReader(new FileReader(currentFile));
            line = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //<editor-fold desc="Project_Part_One.Document Generator">

    /***
     * This function responsible on parsing the Project_Part_One.Document object form the given @bufferedReader,
     * who repesnt the line in the searched file
     * @return Brand new Project_Part_One.Document object
     * @throws IllegalArgumentException
     */
    public Document generateDocTemp() throws IllegalArgumentException {

        StringBuilder docNo = new StringBuilder();
        StringBuilder date = new StringBuilder();
        StringBuilder title = new StringBuilder();
        StringBuilder text = new StringBuilder();
        //Array represent which attribute is being parsed currently
        //0:DOCNO , 1:DATE, 2:TITLE, 3:TEXT
        boolean[] boolArray = {false, false, false, false};
        boolean[] boolHasTag = {false, false, false, false};
        Stack<Integer> indexStack = new Stack<>();

        //running until the end of current DOC
        try {
            while (!(line = bufferedReader.readLine()).contains("</DOC>")) {
                //line = line.replaceAll(" ", "  ");
                //Case 1 the value is in the same line
                if (!boolHasTag[0] && (line.contains("<DOCNO>") && line.contains("</DOCNO>"))) {
                    docNo = getAttributeValue(line, "DOCNO");
                    if(docNo.toString().equals(""))
                        docNo = new StringBuilder(" " + line.replaceAll("[(<DOCNO>)(/DOCNO>)]",""));
                    boolHasTag[0] = true;
                } else if (!boolHasTag[1] && (line.contains("<DATE1>") && line.contains("</DATE1>") || line.contains("<DATE>") && line.contains("</DATE>"))) {
                    date = getAttributeValue(line, "DATE1");
                    boolHasTag[1] = true;

                } else if (!boolHasTag[2] && (line.contains("<TI>") && line.contains("</TI>") || (line.contains("<HEADLINE>") && line.contains("</HEADLINE>")))) {
                    if (line.contains("<TI>")) {
                        title = getAttributeValue(line, "TI");
                        boolHasTag[2] = true;
                    } else {
                        title = getAttributeValue(line, "HEADLINE");
                        boolHasTag[2] = true;
                    }
                } else if (!boolHasTag[3] && line.contains("<TEXT>") && line.contains("</TEXT>")) {
                    title = getAttributeValue(line, "TEXT");
                    boolHasTag[3] = true;
                } else {
                    //Case 2 the value is different line line
                    if (!boolHasTag[0] && line.contains("<DOCNO>")) {
                        setBoolean(0, boolArray, indexStack);
                        boolHasTag[0] = true;
                    } else if (!boolHasTag[1] && line.contains("<DATE>")) {
                        setBoolean(1, boolArray, indexStack);
                        boolHasTag[1] = true;
                    } else if (!boolHasTag[2] && (line.contains("<TI>") || (line.contains("<HEADLINE>")))) {
                        setBoolean(2, boolArray, indexStack);
                        boolHasTag[2] = true;
                    } else if (!boolHasTag[3] && line.contains("<TEXT>")) {
                        setBoolean(3, boolArray, indexStack);
                        boolHasTag[3] = true;
                    }

                    //Adding the line to the turn on Attribute dicided by the bool array
                    if (boolArray[0]) {
                        docNo.append(line.replaceAll("<.*>", " ")+"\n");
                    }
                    if (boolArray[1]) {
                        date.append(line.replaceAll("<.*>", " ")+"\n");
                    }
                    if (boolArray[2]) {
                        title.append(line.replaceAll("<.*>", " ")+"\n");
                    }
                    if (boolArray[3]) {
                        text.append(line.replaceAll("<.*>", " ")+"\n");
                    }

                    if (line.contains("</DOCNO>")) {
                        setBooleanOff(0, boolArray, indexStack);
                    } else if (line.contains("</DATE>")) {
                        setBooleanOff(1, boolArray, indexStack);
                    } else if (line.contains("</TI>") || (line.contains("</HEADLINE>"))) {
                        setBooleanOff(2, boolArray, indexStack);
                    } else if (line.contains("</TEXT>")) {
                        setBooleanOff(3, boolArray, indexStack);
                    }
                }
            }
            return new Document(docNo, date, title, text);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Get Value from tag
    protected StringBuilder getAttributeValue(String line, String element) throws IOException {
        String[] strArr;
        StringBuilder value = new StringBuilder();
        //Contains both of tags in the same line
        if (line.contains("<" + element + ">") && line.contains("</" + element + ">")) {
            strArr = line.split(" ");
            for (int i = 0; i < strArr.length; i++) {
                if (!strArr[i].contains("<" + element + ">") && !strArr[i].contains("</" + element + ">") && !strArr[i].contains("<H3>")) {
                    value =value.append(" " + strArr[i]);
                }
            }
        }
        return value;
    }


    // Set Boolean True in Boolean Array
    //Pushing the needed tag
    protected void setBoolean(int index, boolean[] boolArray, Stack<Integer> indexStack) throws IOException {
        for (int i = 0; i < boolArray.length; i++) {
            boolArray[i] = false;
        }
        boolArray[index] = true;
        indexStack.push(index);

    }

    /***
     * This function responsible to set the the index to false, the @index of the object we found inside the @boolArray
     * @param index
     * @param boolArray
     * @param indexStack
     * @throws IOException
     */
    protected void setBooleanOff(int index, boolean[] boolArray, Stack<Integer> indexStack) throws IOException {
        boolArray[indexStack.pop()] = false;
        if (!indexStack.empty()) {
            boolArray[indexStack.peek()] = true;
        }
    }
    //</editor-fold>


    //</editor-fold>

    //<editor-fold desc="Get Next Doc">

    /**
     * This function return the next avaliable document
     *
     * @return Project_Part_One.Document
     * @throws IOException
     */
    public Document getNextDoc() {
        Document docToReturn = null;
        try {
            if (line == null) {
                return null;
            }
            if (line.contains("<DOC>")) {
                docToReturn = generateDocTemp();
            } else {
                while (!line.contains("<DOC>")) {
                    line = bufferedReader.readLine();
                }
                docToReturn = generateDocTemp();
            }
            while (line != null && !line.contains("<DOC>")) {
                line = bufferedReader.readLine();
            }
            if (line == null && fileIndex < fileArrayLen) {
                if(!fileArray[fileIndex].isDirectory())
                    fileIndex++;
                if(fileIndex < fileArrayLen) {
                    currentFile = new File(fileArray[fileIndex] + "\\" + fileArray[fileIndex].getName());
                    fileIndex++;
                    this.bufferedReader = new BufferedReader(new FileReader(currentFile));
                    line = bufferedReader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(docToReturn != null)
            numOfIndexedDocs++;

        return docToReturn;
    }
    //</editor-fold>


    /**
     * This function return list of documents of given size.
     * @param size
     * @return List<Project_Part_One.Document>
     */
    synchronized public List<Document> getDocs(int size) {
        List<Document> docList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Document doc = getNextDoc();
            if(doc != null)
                docList.add(doc);
            else
                break;
        }
        return docList;
    }

    public int getNumOfIndexedDocs() {
        return numOfIndexedDocs;
    }
}
