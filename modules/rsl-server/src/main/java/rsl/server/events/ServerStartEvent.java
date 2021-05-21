package rsl.server.events;

import rsl.util.pubsub.PubSubEvent;

public class ServerStartEvent extends PubSubEvent {
    public ServerStartEvent() {
        super("");
    }
}
