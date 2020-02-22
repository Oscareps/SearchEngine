package Project_Part_Two;

import Project_Part_One.InfoPackage.DocInfo;


/**
 * This class is responisble of the data of each term in do, containing the TF and DocInfo who has the term
 */
public class TermInfoInDoc {

    private String docNo;
    private int TF;
    private DocInfo docInfo;

    public TermInfoInDoc(String docNo, int TF, DocInfo docInfo) {
        this.docNo = docNo;
        this.TF = TF;
        this.docInfo = docInfo;
        this.docInfo.setDocNum(docNo);
    }

    public String getDocNo() {
        return docNo;
    }

    public int getTF() {
        return TF;
    }

    public DocInfo getDocInfo() {
        return docInfo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public void setTF(int TF) {
        this.TF = TF;
    }

    public void setDocInfo(DocInfo docInfo) {
        this.docInfo = docInfo;
    }
}
