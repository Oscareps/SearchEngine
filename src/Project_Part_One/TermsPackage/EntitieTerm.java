package Project_Part_One.TermsPackage;

import Project_Part_One.Document.Document;

/**
 * Class for Entity term
 */
public class EntitieTerm extends Term{

    private String entitie;


    /**
     * Constructor for the entity term
     * @param entitieTerm String representing the entity term
     * @param doc The documents holding this term
     */
    public EntitieTerm(String entitieTerm, Document doc) {
        super(doc);
        this.entitie = entitieTerm;
    }


    @Override
    public String toString() {
        return "!" + entitie;
    }
}
