package Project_Part_One.ParserPackage;

import Project_Part_One.TermsPackage.Term;
import Project_Part_Two.Query;

import java.util.HashMap;

/**
 * Interface for parser
 */
public interface IParser {

    void parseDocs();

    public HashMap<String, Term> parseQuery(Query query,boolean isStem,boolean isSemantic);

}
