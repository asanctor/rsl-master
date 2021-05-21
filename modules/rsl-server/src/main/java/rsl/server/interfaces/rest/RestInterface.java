package rsl.server.interfaces.rest;

import rsl.server.RSLServer;
import rsl.server.ServerResponse;
import rsl.server.events.InterfaceStartEvent;
import rsl.server.events.InterfaceStopEvent;
import rsl.server.events.ResponseSentEvent;
import rsl.server.events.ServerErrorEvent;
import rsl.server.interfaces.ClientCommunicationInterface;
import java.io.IOException;
import java.util.Properties;

public class RestInterface extends ClientCommunicationInterface {

    private HTTPServer server;

    public RestInterface(String interfaceName, Properties config)
    {
        super(interfaceName, config);
    }

    @Override
    public void start() {
        try {
            int port = Integer.valueOf(this.getProperties().getProperty("port"));
            server = new HTTPServer(this, port);
            server.start();
            RSLServer.pubSubBus.publish(new InterfaceStartEvent(this, port));
        } catch (IOException e) {
            RSLServer.pubSubBus.publish(new ServerErrorEvent("Error in RestInterface", e));
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
            server = null;
        } catch (Exception e) {
            RSLServer.pubSubBus.publish(new ServerErrorEvent("Error while stopping RestInterface", e));
        }
        RSLServer.pubSubBus.publish(new InterfaceStopEvent(this));
    }

    @Override
    public void sendReply(Object clientInterfaceState, ServerResponse response) {
        ServerState state = (ServerState) clientInterfaceState;
        state.setResponse(response);
        RSLServer.pubSubBus.publish(new ResponseSentEvent(response));
    }

    @Override
    public Properties getDefaultProperties()
    {
        Properties props = new Properties();
        props.setProperty("port", "8080");
        return props;
    }

}
