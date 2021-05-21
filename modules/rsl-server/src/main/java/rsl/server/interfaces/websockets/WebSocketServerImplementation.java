package rsl.server.interfaces.websockets;


import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import rsl.server.ClientRequest;
import rsl.server.RSLServer;
import rsl.server.ServerResponse;
import rsl.server.events.ClientConnectEvent;
import rsl.server.events.ClientDisconnectEvent;
import rsl.server.events.RequestReceivedEvent;
import rsl.server.events.ServerErrorEvent;
import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.util.json.JSONProcessor;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class WebSocketServerImplementation extends WebSocketServer {

    private ClientCommunicationInterface clientInterface;

    WebSocketServerImplementation(ClientCommunicationInterface clientInterface, int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        this.clientInterface = clientInterface;
    }

    void sendReply(Object clientState, ServerResponse response)
    {
        WebSocket conn = (WebSocket) clientState;
        String responseJSON = JSONProcessor.toJSON(response);
        conn.send(responseJSON);
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        RSLServer.pubSubBus.publish(new ClientConnectEvent(clientInterface, conn.getRemoteSocketAddress().toString()));
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        RSLServer.pubSubBus.publish(new ClientDisconnectEvent(clientInterface, conn.getRemoteSocketAddress().toString()));
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        System.out.println("in socket msg:" + message);
        ClientRequest req = parseCommand(message);
        RSLServer.pubSubBus.publish(new RequestReceivedEvent(clientInterface, conn, req));
    }

    @Override
    public void onFragment( WebSocket conn, Framedata fragment ) {
        RSLServer.pubSubBus.publish(new ServerErrorEvent("WebSocket interface invoked onFragment() method, unimplemented!", null));
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        RSLServer.pubSubBus.publish(new ServerErrorEvent("WebSocket interface invoked onError() method!", ex));
    }

    @Override
    public void onStart() {

    }

    private ClientRequest parseCommand(String message)
    {
        return JSONProcessor.fromJSON(message, ClientRequest.class);
    }

}
