package rsl.util.pubsub;

public abstract class PubSubEvent {

    protected String busName;
    private String message;
    private long timestamp;


    public PubSubEvent(String msg)
    {
        this.timestamp = System.currentTimeMillis();
        this.message = msg;
    }

    public String getMessage()
    {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getBusName() { return busName; }

}
