package rsl.server.interfaces;

import rsl.server.interfaces.rest.RestInterface;
import rsl.server.interfaces.websockets.WebSocketInterface;

import java.util.ArrayList;

public class InterfaceManager {

    private static ArrayList<ClientCommunicationInterface> interfaces = new ArrayList<>();
    static {
        interfaces.add(new WebSocketInterface("WebSocket",null));
        interfaces.add(new RestInterface("REST",null));
    }

    public static void startInterfaces()
    {
        for (ClientCommunicationInterface clientInterface: interfaces) {
            clientInterface.start();
        }
    }

    public static void stopInterfaces()
    {
        for (ClientCommunicationInterface clientInterface: interfaces) {
            clientInterface.stop();
        }
    }

}
