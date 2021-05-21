package rsl.persistence;

import java.util.HashMap;

public class PersistenceQuery {

    private String query = "";
    private HashMap<String, Object> parameters = new HashMap<>();

    public PersistenceQuery(){}

    public PersistenceQuery(String query)
    {
            this.query = query;
    }

    public PersistenceQuery(String query, HashMap<String, Object> parameters)
    {
        this.query = query;
        this.parameters = parameters;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setParameter(String parameterName, Object value) {
        parameters.put(parameterName, value);
    }

    public void setParameters(HashMap<String, Object> parameters)
    {
        this.parameters = parameters;
    }

    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    public String getQuery() {
        return query;
    }
}
