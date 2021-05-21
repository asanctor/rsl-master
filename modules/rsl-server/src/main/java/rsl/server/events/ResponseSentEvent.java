package rsl.server.events;

import rsl.server.ServerResponse;
import rsl.util.pubsub.PubSubEvent;

public class ResponseSentEvent extends PubSubEvent {

    private ServerResponse response;

    public ResponseSentEvent(ServerResponse response) {
        super("");
        this.response = response;
    }


    public ServerResponse getServerResponse() {
        return response;
    }
}
