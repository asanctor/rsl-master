package rsl.server;

public class ServerResponse {

    private int statusCode;
    private String message;
    private Object result;

    public static final int STATUS_OK = 200;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_SERVER_ERROR = 200;



    public <T> ServerResponse(int statusCode, String message, T result)
    {
        this.statusCode = statusCode;
        this.message = message;
        this.result = result;
    }

    public <T> void setResult(T result)
    {
        this.result = result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

}
