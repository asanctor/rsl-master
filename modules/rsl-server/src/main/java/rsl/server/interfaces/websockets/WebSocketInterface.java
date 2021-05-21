package rsl.server.interfaces.websockets;

import rsl.server.RSLServer;
import rsl.server.ServerResponse;
import rsl.server.events.InterfaceStartEvent;
import rsl.server.events.InterfaceStopEvent;
import rsl.server.events.ResponseSentEvent;
import rsl.server.events.ServerErrorEvent;
import rsl.server.interfaces.ClientCommunicationInterface;

import java.net.UnknownHostException;
import java.util.Properties;


public class WebSocketInterface extends ClientCommunicationInterface {

    private WebSocketServerImplementation server;

    public WebSocketInterface(String interfaceName, Properties config)
    {
        super(interfaceName, config);
    }

    @Override
    public void start() {
        try {
            int port = Integer.valueOf(this.getProperties().getProperty("port"));
            server = new WebSocketServerImplementation(this, port);
            server.start();
            RSLServer.pubSubBus.publish(new InterfaceStartEvent(this, port));
        } catch (UnknownHostException e) {
            RSLServer.pubSubBus.publish(new ServerErrorEvent("Error in WebSocketInterface", e));
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            RSLServer.pubSubBus.publish(new ServerErrorEvent("Error while stopping WebSocketInterface", e));
        }
        RSLServer.pubSubBus.publish(new InterfaceStopEvent(this));
    }

    @Override
    public void sendReply(Object clientInterfaceState, ServerResponse response) {
        server.sendReply(clientInterfaceState, response);
        RSLServer.pubSubBus.publish(new ResponseSentEvent(response));
    }

    @Override
    public Properties getDefaultProperties()
    {
        Properties props = new Properties();
        props.setProperty("port", "3556");
        return props;
    }

}
