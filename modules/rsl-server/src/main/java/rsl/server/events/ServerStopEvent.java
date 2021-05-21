package rsl.server.events;

import rsl.util.pubsub.PubSubEvent;

public class ServerStopEvent extends PubSubEvent {
    public ServerStopEvent() {
        super("");
    }
}
