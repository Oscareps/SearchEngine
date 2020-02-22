package Project_Part_Two;

/**
 * This object represent the query formed from ID, Title and the Description
 */
public class Query {

    private String queryID;
    private String queryTitle;
    private String queryDesc;

    public Query(String queryID, String queryTitle ,String queryDesc) {
        this.queryID = queryID;
        this.queryTitle = queryTitle;
        this.queryDesc = queryDesc;

    }

    public String getQueryID() {
        return queryID;
    }

    public String getQueryTitle() {
        return queryTitle;
    }

    public String getQueryDesc() {
        return queryDesc;
    }
}
