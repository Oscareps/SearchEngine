package GUIPackage;

import Project_Part_One.ComperatorPackage.MapEntryStringComparator;
import Project_Part_One.ComperatorPackage.PairIntegerBackwardsComparator;
import Project_Part_One.IndexerPackage.Indexer;
import Project_Part_One.InfoPackage.DocInfo;
import Project_Part_One.InfoPackage.TermInfo;
import Project_Part_One.ParserPackage.RegexParser;
import Project_Part_One.ReadFilePackage.ReadFile;
import Project_Part_One.TermsPackage.WordTerm;
import Project_Part_Two.Query;
import Project_Part_Two.Searcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.Button;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SuppressWarnings("ALL")
public class Controller {

    //View Variable
    private Scene mainScene;
    private Stage mainStage;

    //The Corresponding variables
    private String pathToCorpus;
    private String pathToIndexer;
    private String pathToQueryFile;
    private String pathToReslutFile;
    private String pathToIndex;
    private String pathToindexData;
    private Boolean isStem;
    private Boolean isSemantic;
    private Boolean isSave;
    private Boolean pathCorpusChoose = false;
    private Boolean pathIndexChoose = false;
    private Boolean pathQueryFileChoose = false;
    private Boolean pathResultChoose = false;

    //The GUI Variable
    @FXML
    private TextField textField_toCorpus;
    @FXML
    private TextField textField_toIndex;
    @FXML
    private TextField textField_fileQuery;
    @FXML
    private TextField textField_query;
    @FXML
    private TextField textField_toResultFile;
    @FXML
    private CheckBox checkBox_Steam;
    @FXML
    private CheckBox checkBox_Semantic;
    @FXML
    private CheckBox checkBox_PresentsEnititeis;
    @FXML
    private Button buttonXmlOeffnen;

    Hashtable<String, TermInfo> termDict;
    Hashtable<String, DocInfo> docDict;
    List<Pair<String, Double>> searchResults;
    List<DocInfo> listResultDocInfo;


    /**
     * Constructor for the Controller
     */
    public Controller() {

        isStem = false;
        isSemantic = false;
        RegexParser.setStem(false);
    }


    /**
     * Uploads The dictionary given by the GUI to termDict variable
     */
    public void uploadDicToRam() {

        if (!pathIndexChoose || Files.notExists(Paths.get(pathToIndexer + "\\Output")) || Files.notExists(Paths.get(pathToIndexer + "\\stop_words.txt"))) {
            showAlert("Invalid Path inserted");
            return;
        }

        if (!checkIfIndexFolderExists()) {
            showAlert("Right Index doesn't exists");
            return;
        }

        setPathToIndexData();


        try {
            //<editor-fold desc="Uploading term dictionary">

            File termDictionary = new File(pathToindexData + "\\Term Dictionary\\dictionary");
            BufferedReader reader = new BufferedReader(new FileReader(termDictionary));
            String line;
            this.termDict = new Hashtable<>();
            while ((line = reader.readLine()) != null) {
                String[] lineSplitted = line.split("[:|]");
                termDict.put(lineSplitted[0], new TermInfo(Integer.parseInt(lineSplitted[1]), lineSplitted[2]));
            }
            //</editor-fold>

            //<editor-fold desc="Uploading document dictionary">
            File docDictionary = new File(pathToindexData + "\\Document Dictionary\\dictionary");
            reader = new BufferedReader(new FileReader(docDictionary));
            this.docDict = new Hashtable<>();
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    String[] lineSplitted = line.split("[:#]");
                    DocInfo newDoc = new DocInfo(Integer.parseInt(lineSplitted[1]), Integer.parseInt(lineSplitted[2]), Integer.parseInt(lineSplitted[3]));
                    newDoc.setTopEntities(stringToTopEntities(lineSplitted[4]));
                    docDict.put(lineSplitted[0], newDoc);

                } else
                    DocInfo.setAvgdl(Double.parseDouble(line));
            }
            //</editor-fold>

        } catch (IOException e) {
            showAlert("Invalid Path inserted");
            this.termDict = null;
            this.docDict = null;
            return;
        }

        WordTerm.setStopWords(textField_toIndex.getText());

        showAlert("Dictionary Uploaded");

    }


    /**
     * Genreating the top entites of each document
     *
     * @param topEntitiesString
     * @return
     */
    private PriorityQueue<Pair<String, Integer>> stringToTopEntities(String topEntitiesString) {
        PriorityQueue<Pair<String, Integer>> res = new PriorityQueue<>(5, new PairIntegerBackwardsComparator());
        String[] entitiesPairs = topEntitiesString.split(";");
        for (int i = 0; i < entitiesPairs.length; i++) {
            String[] entity_weight = entitiesPairs[i].split(",");
            res.add(new Pair<>(entity_weight[0], Integer.parseInt(entity_weight[1])));
        }
        return res;
    }


    /**
     * Delete all the output folders and files of the Search Engine
     */
    public void deleteIndex() {


        if (Files.notExists(Paths.get(pathToIndexer + "\\Output"))) {
            showAlert("Invalid path");
            return;
        }

        deleteFolderOrFile(pathToIndexer + "\\Output");

        showAlert("Index Deleted");

    }


    /**
     * Recursivley deletes the folder Output and all the files and directories inside it
     *
     * @param path
     */
    private void deleteFolderOrFile(String path) {
        File file = new File(path);
        String[] entries = file.list();
        for (String s : entries) {
            File currentFile = new File(file.getPath(), s);
            if (currentFile.isDirectory())
                deleteFolderOrFile(currentFile.getPath());

            currentFile.delete();
        }
        file.delete();
    }


    /**
     * Opens windows file explorer to choose the wanted path for the corpus and updates pathToCorpus variable with it
     */
    public void chooseDirToCorpus() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(mainStage);

        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            this.pathToCorpus = selectedDirectory.getAbsolutePath();
            this.pathCorpusChoose = true;
            textField_toCorpus.setText(this.pathToCorpus);
        }
    }


    /**
     * Opens windows file explorer to choose the wanted path for the Posting files and dictionaries and updates pathToIndexer variable with it
     */
    public void chooseDirToIndex() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(mainStage);

        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            this.pathToIndexer = selectedDirectory.getAbsolutePath();
            this.pathIndexChoose = true;
            textField_toIndex.setText(this.pathToIndexer);
            this.setStem();
            if (isStem)
                pathToIndex = pathToIndexer + "\\Output\\Stem On";
            else
                pathToIndex = pathToIndexer + "\\Output\\Stem Off";

            if (checkIfIndexFolderExists()) {
                setPathToIndexData();
            }

        }
    }


    /**
     * Setting the pathToIndexData variable according to the pathToIndex variable
     */
    private void setPathToIndexData() {
        File indexDirectory = new File(pathToIndex);
        int indexCount = indexDirectory.list().length - 1;
        this.pathToindexData = pathToIndex + "\\Index" + indexCount;
    }


    /**
     * Opens windows file explorer to choose the wanted path for the query and updates pathToQuery variable with it
     */
    public void chooseQueryFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open query file");

        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile == null) {
            //No Directory selected
        } else {
            this.pathToQueryFile = selectedFile.getAbsolutePath();
            this.pathQueryFileChoose = true;
            textField_fileQuery.setText(this.pathToQueryFile);
        }
    }


    public void chooseDirToResultFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(mainStage);

        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            this.pathToReslutFile = selectedDirectory.getAbsolutePath();
            this.pathResultChoose = true;
            textField_toResultFile.setText(this.pathToReslutFile);
        }
    }


    /**
     * Pop message with the given text on the screen
     *
     * @param alertMessage The message to show
     */
    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }


    /**
     * Shows The dictionary that is currently on the Ram
     */
    public void presentDictionary() {
        try {
            if (termDict != null) {
                Stage stage = new Stage();
                stage.setTitle("Dictionary");

                ObservableList<TermData> termData = FXCollections.observableArrayList();
                for (Map.Entry<String, TermInfo> entry : termDict.entrySet()) {
                    termData.add(new TermData(entry.getKey(), entry.getValue().getFrequencyInCorpus()));
                }

                TableColumn firstNameCol = new TableColumn();
                firstNameCol.setText("term");
                firstNameCol.setCellValueFactory(new PropertyValueFactory("term"));
                firstNameCol.setComparator(String.CASE_INSENSITIVE_ORDER);

                TableColumn lastNameCol = new TableColumn();
                lastNameCol.setText("Frequency");
                lastNameCol.setCellValueFactory(new PropertyValueFactory("numOfAppearences"));

                TableView tableView = new TableView();
                tableView.setItems(termData);
                tableView.getColumns().addAll(firstNameCol, lastNameCol);
                tableView.getSortOrder().add(firstNameCol);

                Scene scene = new Scene(tableView);
                stage.setScene(scene);
                stage.show();

            }

        } catch (Exception e) {

        }
    }


    /**
     * Main function. Parsing all of the documents in the Corpus and Indexing the terms inside it to dictionary and Posting Files.
     * Saves all kind of detailes  on the documents
     */
    public void indexCorpus() {

        if (!pathCorpusChoose || !pathIndexChoose) {
            showAlert("Invalid Path inserted");
            return;
        }

        //<editor-fold desc="Init directories">
        File directory = new File(pathToIndexer + "\\Output");
        if (!directory.exists())
            directory.mkdirs();

        File indexDirectory = new File(pathToIndex);

        if (!indexDirectory.exists())
            indexDirectory.mkdirs();

        int indexCount = indexDirectory.list().length;


        try {
            Files.createDirectory(Paths.get(pathToIndex + "\\Index" + indexCount));
            Files.createDirectory(Paths.get(pathToIndex + "\\Index" + indexCount + "\\Temp"));
            Files.createDirectory(Paths.get(pathToIndex + "\\Index" + indexCount + "\\Posting Files"));
            Files.createDirectory(Paths.get(pathToIndex + "\\Index" + indexCount + "\\Term Dictionary"));
            Files.createDirectory(Paths.get(pathToIndex + "\\Index" + indexCount + "\\Document Dictionary"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        //<editor-fold desc="Init vars to index">
        termDict = new Hashtable<>();
        docDict = new Hashtable<>();

        ExecutorService pool = Executors.newFixedThreadPool(5);
        ExecutorService pool2 = Executors.newFixedThreadPool(5);

        ReadFile fileReader = new ReadFile(pathToCorpus);

        WordTerm.setStopWords(pathToCorpus);

        double startTime = System.currentTimeMillis();

        RegexParser.initLocks();
        //</editor-fold>

        //<editor-fold desc="Parsing">
        for (int i = 0; i < 5; i++) {
            RegexParser parser1 = new RegexParser(fileReader, 5000, pathToIndex + "\\Index" + indexCount, 20);
            pool.execute(parser1);
        }

        pool.shutdown();

        while (!pool.isTerminated()) {
        }
        //</editor-fold>

        //<editor-fold desc="Indexing">
        for (int i = 0; i < 5; i++) {
            Indexer indexer1 = new Indexer(pathToIndex + "\\Index" + indexCount, termDict, docDict, (20 / 5) * i, (20 / 5) + ((20 / 5) * i), 200);
            pool2.execute(indexer1);
        }

        pool2.shutdown();

        while (!pool2.isTerminated()) {
        }
        //</editor-fold>

        //<editor-fold desc="Writing dictionaries">
        ArrayList<Map.Entry<String, TermInfo>> termDictList = new ArrayList<>(termDict.entrySet());
        Collections.sort(termDictList, new MapEntryStringComparator());

        StringBuilder termDictionary = new StringBuilder();
        StringBuilder docDictionary = new StringBuilder();

        // Preparing term dictionary to writing proccess
        for (Map.Entry<String, TermInfo> entry : termDictList) {
            termDictionary.append(entry.getKey() + ":" + entry.getValue().getFrequencyInCorpus() + "|" + entry.getValue().getFileNameLocation() + "," + entry.getValue().getLocationInFile() + "\n");
        }

        // Preparing document dictionary to writing proccess
        double docLengthSum = 0;
        for (Map.Entry<String, DocInfo> entry : docDict.entrySet()) {
            docDictionary.append(entry.getKey() + ":" + entry.getValue().getNumOfTermsInDoc() + "#" + entry.getValue().getMaxTF() + "#" + entry.getValue().getDocVectorLength() + "#");
            docDictionary.append(topEntityToString(entry.getValue().getTopEntities()) + "\n");
            docLengthSum += entry.getValue().getNumOfTermsInDoc();
        }
        DocInfo.setAvgdl(docLengthSum / docDict.size());
        docDictionary.append("" + (docLengthSum / docDict.size()));

        // Writing all dictionaries and deleting temp directory
        try {
            Files.delete(Paths.get(pathToIndex + "\\Index" + indexCount + "\\Temp"));
            Files.write(Paths.get(pathToIndex + "\\Index" + indexCount + "\\Term Dictionary\\Dictionary"), termDictionary.toString().getBytes());
            Files.write(Paths.get(pathToIndex + "\\Index" + indexCount + "\\Document Dictionary\\Dictionary"), docDictionary.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        double endTime = System.currentTimeMillis();

        showAlert("Number Of Indexed Documents: " + fileReader.getNumOfIndexedDocs() +
                "\nNumber Of Unique Terms In Corpus: " + termDict.size() +
                "\nIndex Total Time: " + ((endTime - startTime) / 1000) + " Seconds");


    }

    /**
     * Transform a list of pairs of entities and weights to string
     *
     * @param topEntities PriorityQueue of pairs to transform into string
     * @return String representing the PriorityQueue
     */
    private String topEntityToString(PriorityQueue<Pair<String, Integer>> topEntities) {
        String res = ";";
        int topEntitySize = topEntities.size();
        for (int i = 0; i < topEntitySize; i++) {
            Pair<String, Integer> entity = topEntities.poll();
            String entityString = entity.getKey() + "," + entity.getValue();
            if (i != 0)
                entityString += ";";
            res = entityString + res;
        }
        return res;
    }

    /**
     * Turning the stem option on or off by the check box in the GUI
     */
    public void setStem() {
        if (isStem = checkBox_Steam.isSelected()) {
            pathToIndex = pathToIndexer + "\\Output\\Stem On";
            isStem = true;
        } else {
            pathToIndex = pathToIndexer + "\\Output\\Stem Off";
            isStem = false;
        }
        RegexParser.setStem(isStem);
    }

    /**
     * Turning the semantic option on or off by the check box in the GUI
     */
    public void setSemantic() {
        this.isSemantic = checkBox_Semantic.isSelected();

    }


    /**
     * Turning the save option on or off by the check box in the GUI
     */
    public void setPresentEntites() {
        this.isSave = checkBox_PresentsEnititeis.isSelected();
    }

    /**
     * Inner Class for the presentDictionary
     */
    public class TermData {
        String term;
        int numOfAppearences;

        public TermData(String term, int numOfAppearences) {
            this.term = term;
            this.numOfAppearences = numOfAppearences;
        }

        public String getTerm() {
            return term;
        }

        public int getNumOfAppearences() {
            return numOfAppearences;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public void setNumOfAppearences(int numOfAppearences) {
            this.numOfAppearences = numOfAppearences;
        }
    }

    /**
     * Searching for the most relavent documents in the dictionary by the given query in the textBox
     */
    public void searchSingleQuery() {
        String query = textField_query.getText();
        String path = textField_fileQuery.getText();
        if (pathToIndexer == null) {
            showAlert("No file found");
            return;
        }
        if (termDict == null) {
            showAlert("No dictionary found, please upload first");
            return;
        }
        if (pathToReslutFile == null) {
            showAlert("No directory was choosen for result file");
            return;
        }
        if (query == null || query.length() == 0) {
            return;
        }

        Searcher searcher = new Searcher(termDict, docDict, new RegexParser(), pathToindexData);
        Query q = new Query("351", query + " "+ query.toLowerCase(), "");
        searchResults = searcher.search(q, this.isStem, this.isSemantic);

        List<Pair<String, List<Pair<String, Double>>>> fileResults = new ArrayList<>();
        this.searchResults = new ArrayList<>();
        List<Pair<String, Double>> singleQueryResList = searcher.search(q, this.isStem, this.isSemantic);
        fileResults.add(new Pair<>(q.getQueryID(), singleQueryResList));
        searchResults.addAll(singleQueryResList);
        genrateOutPutFile(fileResults);
        toDocInfoResults();
        prsenentResults();
    }

    private boolean checkIfIndexFolderExists() {
        File indexDirectory = new File(pathToIndex);
        return Files.exists(Paths.get(pathToIndex));
    }

    /**
     * Updates the variable listResultDocInfo
     */
    private void toDocInfoResults() {
        List<DocInfo> listDocInfo = new ArrayList<>();
        for (Pair<String, Double> pair : searchResults) {
            listDocInfo.add(docDict.get(pair.getKey()));
        }
        this.listResultDocInfo = listDocInfo;
    }

    /**
     * This function present the top document
     */
    public void prsenentResults() {
        if (listResultDocInfo == null) return;
        if (this.checkBox_PresentsEnititeis.isSelected()) {
            for (DocInfo docInfo : listResultDocInfo) {
                docInfo.setTopFiveEntities();
            }
            prsenentResultsWithEntite();
        } else {
            Stage stage = new Stage();
            stage.setTitle("Results");
            TableColumn docColum = new TableColumn("Doc ID");
            //TableColumn buttonColumn = new TableColumn("Present top 5 Entities");
            ObservableList<DocInfo> docData = FXCollections.observableArrayList(listResultDocInfo);
            docColum.setCellValueFactory(new PropertyValueFactory<>(""));

            docColum.setCellValueFactory(new PropertyValueFactory("docID"));
            docColum.setPrefWidth(300);

            TableView tableView = new TableView();
            tableView.setItems(docData);
            tableView.getColumns().addAll(docColum);
            tableView.setPrefWidth(300);
            Scene scene = new Scene(tableView);
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Present the results with the top entites if was choosen
     */
    public void prsenentResultsWithEntite() {

        Stage stage = new Stage();
        stage.setTitle("Results");
        ObservableList<DocInfo> docData = FXCollections.observableArrayList(listResultDocInfo);

        TableColumn firstNameCol = new TableColumn();
        firstNameCol.setText("Doc ID");
        firstNameCol.setCellValueFactory(new PropertyValueFactory("docID"));
        TableColumn lastNameCol = new TableColumn();

        lastNameCol.setText("Top Enitites");
        lastNameCol.setCellValueFactory(new PropertyValueFactory("top"));
        TableView tableView = new TableView();

        tableView.setItems(docData);
        tableView.getColumns().addAll(firstNameCol, lastNameCol);

        Scene scene = new Scene(tableView);
        stage.setScene(scene);
        stage.show();


    }

    /**
     * This function responisble for searching with file
     */
    public void searchTheFile() {

        String path = textField_fileQuery.getText();
        if (pathToIndexer == null) {
            showAlert("No index found");
            return;
        }
        if (termDict == null) {
            showAlert("No dictionary found, please upload first");
            return;
        }
        if (pathToQueryFile == null) {
            showAlert("No file found");
            return;
        }
        if (pathToReslutFile == null) {
            showAlert("No directory was choosen for result file");
            return;
        }

        Searcher searcher = new Searcher(termDict, docDict, new RegexParser(), pathToindexData);
        List<Query> queryList = searcher.generateQueriesListFromFile(path);
        List<Pair<String, List<Pair<String, Double>>>> fileResults = new ArrayList<>();
        this.searchResults = new ArrayList<>();
        for (Query q : queryList) {
//            long startTime = System.currentTimeMillis();
            List<Pair<String, Double>> singleQueryResList = searcher.search(q, this.isStem, this.isSemantic);
            fileResults.add(new Pair<>(q.getQueryID(), singleQueryResList));
            searchResults.addAll(singleQueryResList);
//            long estimatedTime = System.currentTimeMillis() - startTime;
//            System.out.println(estimatedTime);
        }
        genrateOutPutFile(fileResults);
        toDocInfoResults();
    }

    /**
     * This function responisble creating result file for usage with TrecAvel
     */
    private void genrateOutPutFile(List<Pair<String, List<Pair<String, Double>>>> resultList) {

        StringBuilder resultFile = new StringBuilder();
        for (Pair<String, List<Pair<String, Double>>> pair : resultList) {
            String queryID = pair.getKey().toString();
            for (Pair<String, Double> resultPair : pair.getValue()) {
                resultFile.append(queryID + " 0" + resultPair.getKey() + " " + resultPair.getValue() + " 0 mt+\n");
            }
        }
        try {
            Files.write(Paths.get(this.pathToReslutFile + "\\results.txt"), resultFile.toString().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
        //        this.pathToReslutFile
    }
}



