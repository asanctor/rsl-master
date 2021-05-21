package rsl.util.pubsub;

public class ErrorEvent extends PubSubEvent{

    private Exception exception;

    public ErrorEvent(String msg,Exception exception){
        super(msg);
        this.exception = exception;
    }

    public Exception getException(){
        return exception;
    }

}