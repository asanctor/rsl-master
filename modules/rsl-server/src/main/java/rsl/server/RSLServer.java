package rsl.server;

import rsl.core.RSL;
import rsl.server.events.ServerStartEvent;
import rsl.server.events.ServerStopEvent;
import rsl.server.interfaces.InterfaceManager;
import rsl.util.pubsub.PubSubBus;

public class RSLServer {

    private static ServerEventLogger serverEventLogger = new ServerEventLogger();
    public static PubSubBus pubSubBus = new PubSubBus("RSLServer", true, serverEventLogger);
    private static RequestHandler handler = new RequestHandler();

    public static void main(String [] args)
    {
        RSL.loadSchemaClasses();
        RSLServer.start();
    }

    public static void start()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(RSLServer::stop));
        RSLServer.pubSubBus.publish(new ServerStartEvent());
        HTTPStoredContentHoster.start();
        InterfaceManager.startInterfaces();
    }

    public static void stop()
    {
        InterfaceManager.stopInterfaces();
        HTTPStoredContentHoster.stop();
        RSLServer.pubSubBus.publish(new ServerStopEvent());
    }

    public static void enableEventLoggerOutput()
    {
        serverEventLogger.enableOutput();
    }

    public static void disableEventLoggerOutput()
    {
        serverEventLogger.disableOutput();
    }

    public static String getVersion()
    {
        return RSLServer.class.getPackage().getImplementationVersion();
    }
}
