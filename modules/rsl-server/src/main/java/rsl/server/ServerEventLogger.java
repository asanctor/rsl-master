package rsl.server;

import rsl.server.events.*;
import rsl.util.Log;
import rsl.util.pubsub.PubSubEventLogger;


public class ServerEventLogger extends PubSubEventLogger {

    public String handleServerErrorEvent(ServerErrorEvent event)
    {
        return "Server Error: " + event.getException().getMessage();
    }

    public String handleServerStartEvent(ServerStartEvent event)
    {
        return "RSL Server Started";
    }

    public String handleServerStopEvent(ServerStopEvent event)
    {
        return "RSL Server Stopped";
    }

    public String handleInterfaceStartEvent(InterfaceStartEvent event)
    {
        String interfaceName = event.getClientInterface().getInterfaceName();
        return String.format("Interface %s Started (port %d)", interfaceName, event.getPort());
    }

    public String handleInterfaceStopEvent(InterfaceStopEvent event)
    {
        String interfaceName = event.getClientInterface().getInterfaceName();
        return String.format("Interface %s Stopped", interfaceName);
    }

    public String handleContentHosterStartEvent(ContentHosterStartEvent event)
    {
        return String.format("ContentHoster Started (port %d)", event.getPort());
    }

    public String handleContentHosterStopEvent(ContentHosterStopEvent event)
    {
        return "ContentHoster Stopped";
    }

    public String handleClientConnectEvent(ClientConnectEvent event)
    {
        String interfaceName = event.getClientInterface().getInterfaceName();
        return String.format("Client Connected via %s: %s", interfaceName, event.getClientID());
    }

    public String handleClientDisconnectEvent(ClientDisconnectEvent event)
    {
        String interfaceName = event.getClientInterface().getInterfaceName();
        return String.format("Client Disconnected via %s: %s", interfaceName, event.getClientID());
    }

    public String handleRequestReceivedEvent(RequestReceivedEvent event)
    {
        if(event == null) { Log.debug("NO EVENT"); }
        if(event.getRequest() == null) { Log.debug("NO REQUEST"); }
        if(event.getRequest().getCommand() == null) { Log.debug("NO COMMAND"); }

        String command = event.getRequest().getCommand();
        String params = event.getRequest().getQueryParameters().toString();
        return String.format("Request Received (command: %s, parameters: %s)", command, params);
    }

    public String handleResponseSentEvent(ResponseSentEvent event)
    {
        int resultCode = event.getServerResponse().getStatusCode();
        String message = event.getServerResponse().getMessage();
        return String.format("Response Sent (responseCode: %d, message: '%s')", resultCode, message);
    }



}
