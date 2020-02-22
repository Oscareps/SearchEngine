package Project_Part_Two;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class Word2Vec {

    HashMap<String,List<String>> semanticTerms = new HashMap<>();
    List<String> terms;
    public Word2Vec(List<String> terms){
        try {
            this.terms =terms;
            Word2VecModel word2VecModel = Word2VecModel.fromTextFile(new File("word2vec.c.output.model.txt"));
            com.medallia.word2vec.Searcher semanticSearcher = word2VecModel.forSearch();
            int numOfResultInList = 3;
            for(String queryTerm : terms){
                List<com.medallia.word2vec.Searcher.Match> matches = semanticSearcher.getMatches((queryTerm),numOfResultInList);
                List<String> matchForQuery = new ArrayList<>();
                for(com.medallia.word2vec.Searcher.Match match : matches){
                    matchForQuery.add(match.match());
                }
                semanticTerms.put(queryTerm,matchForQuery);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Searcher.UnknownWordException e) {
       //     e.printStackTrace();
        }
    }
    public HashMap<String,List<String>> getSemanticForQuery(){
        return this.semanticTerms;
    }
    public String getQueryAfterSematicAdd(){
        String newWords = "";
        List<String> semanticList;
        for (String term:this.terms) {
            if (semanticTerms.containsKey(term)) {
                semanticList = semanticTerms.get(term);
                for (String semanticTerm : semanticList) {
                    newWords = newWords + " "+ semanticTerm;
                }
            }
        }
        return newWords;

    }

}
