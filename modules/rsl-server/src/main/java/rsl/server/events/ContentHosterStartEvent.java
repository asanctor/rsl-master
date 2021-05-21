package rsl.server.events;

import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.util.pubsub.PubSubEvent;

public class ContentHosterStartEvent extends PubSubEvent {

    private int port;

    public ContentHosterStartEvent(int port) {
        super("");
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
