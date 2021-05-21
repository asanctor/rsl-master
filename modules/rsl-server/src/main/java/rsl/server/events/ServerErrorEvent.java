package rsl.server.events;

import rsl.util.pubsub.PubSubEvent;

public class ServerErrorEvent extends PubSubEvent {

    private Exception exception = null;

    public ServerErrorEvent(String msg, Exception exception) {
        super(msg);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

}
