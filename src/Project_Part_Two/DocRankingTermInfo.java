package Project_Part_Two;

import Project_Part_One.InfoPackage.TermInfo;
import Project_Part_One.TermsPackage.Term;

/**
 * Responsible for Data of the the documents and term,
 */
public class DocRankingTermInfo {

    private String docID;
    private Term term;
    private int tfInDoc;
    private TermInfo termInfo;

    public DocRankingTermInfo(String docID ,Term term, int tfInDoc, TermInfo termInfo) {
        this.term = term;
        this.tfInDoc = tfInDoc;
        this.termInfo = termInfo;
        this.docID = docID;
    }

    public Term getTerm() {
        return term;
    }

    public int getTfInDoc() {
        return tfInDoc;
    }

    public TermInfo getTermInfo() {
        return termInfo;
    }

    public String getDocID() {
        return docID;
    }
}
