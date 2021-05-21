package rsl.server.events;

import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.util.pubsub.PubSubEvent;

public class InterfaceStartEvent extends PubSubEvent {

    private ClientCommunicationInterface clientInterface;
    private int port;

    public InterfaceStartEvent(ClientCommunicationInterface clientInterface, int port) {
        super("");
        this.clientInterface = clientInterface;
        this.port = port;
    }

    public ClientCommunicationInterface getClientInterface() {
        return clientInterface;
    }

    public int getPort() {
        return port;
    }
}
