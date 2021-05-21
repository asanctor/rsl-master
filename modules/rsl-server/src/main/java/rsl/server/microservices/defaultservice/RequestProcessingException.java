package rsl.server.microservices.defaultservice;

public class RequestProcessingException extends Exception {

    private int statusCode;

    RequestProcessingException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
