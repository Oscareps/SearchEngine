package Project_Part_Two;

import Project_Part_One.InfoPackage.TermInfo;
import Project_Part_One.TermsPackage.Term;

import java.util.List;

public class TermInfoInCorpus {

    private Term term;
    private TermInfo termInfo;
    private List<TermInfoInDoc> docListInfo;

    public TermInfoInCorpus(Term term, TermInfo termInfo, List<TermInfoInDoc> docListInfo) {
        this.term = term;
        this.termInfo = termInfo;
        this.docListInfo = docListInfo;
    }

    public Term getTerm() {
        return term;
    }

    public TermInfo getTermInfo() {
        return termInfo;
    }

    public List<TermInfoInDoc> getDocListInfo() {
        return docListInfo;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public void setTermInfo(TermInfo termInfo) {
        this.termInfo = termInfo;
    }

    public void setDocListInfo(List<TermInfoInDoc> docListInfo) {
        this.docListInfo = docListInfo;
    }
}
