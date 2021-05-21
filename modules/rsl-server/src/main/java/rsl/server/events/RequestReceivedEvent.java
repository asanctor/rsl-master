package rsl.server.events;

import rsl.server.ClientRequest;
import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.util.pubsub.PubSubEvent;

public class RequestReceivedEvent extends PubSubEvent {

    private ClientRequest request;
    private ClientCommunicationInterface clientInterface;
    private Object clientInterfaceState;

    public RequestReceivedEvent(ClientCommunicationInterface clientInterface, Object clientInterfaceState, ClientRequest request) {
        super("");
        this.clientInterface = clientInterface;
        this.clientInterfaceState = clientInterfaceState;
        this.request = request;
        System.out.println("in request receiver, interface" + clientInterface + " state: " + clientInterfaceState + " request: " + request.toString());
    }

    public ClientRequest getRequest() {
        return request;
    }

    public ClientCommunicationInterface getClientInterface() {
        return clientInterface;
    }

    public Object getClientInterfaceState() {
        return clientInterfaceState;
    }
}
