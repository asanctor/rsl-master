package rsl.server.events;

import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.util.pubsub.PubSubEvent;

public class InterfaceStopEvent extends PubSubEvent {
    private ClientCommunicationInterface clientInterface;

    public InterfaceStopEvent(ClientCommunicationInterface clientInterface) {
        super("");
        this.clientInterface = clientInterface;
    }

    public ClientCommunicationInterface getClientInterface() {
        return clientInterface;
    }
}
