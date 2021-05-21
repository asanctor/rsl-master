package rsl.server.events;

import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.util.pubsub.PubSubEvent;

public class ClientDisconnectEvent extends PubSubEvent {

    private String clientID = "";
    private ClientCommunicationInterface clientInterface;


    public ClientDisconnectEvent(ClientCommunicationInterface clientInterface, String clientID) {
        super("");
        this.clientID = clientID;
        this.clientInterface = clientInterface;
    }

    public String getClientID() {
        return clientID;
    }

    public ClientCommunicationInterface getClientInterface() {
        return clientInterface;
    }

}
