package rsl.server;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import rsl.server.events.RequestReceivedEvent;
import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.server.microservices.Microservice;
import rsl.server.microservices.MicroserviceManager;
import rsl.util.Log;

public class RequestHandler {

    RequestHandler()
    {
        RSLServer.pubSubBus.subscribe(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRequest(RequestReceivedEvent event)
    {
        ClientRequest req = event.getRequest();

        // if no service is specified, the DefaultService will handle the request
        String targetService = req.getServiceName();
        if(targetService.equals(""))
        {
            targetService = "default";
        }

        // check if the service can handle the command
        boolean isValidCommand = MicroserviceManager.isValidCommand(targetService, req.getCommand());
        if(!isValidCommand)
        {
            String msg = String.format("Service '%s' does not support the command '%s'", targetService, req.getCommand());
            ServerResponse res = new ServerResponse(500, msg, "");
            sendResponse(res, event.getClientInterface(), event.getClientInterfaceState());
            return;
        }

        Microservice service = MicroserviceManager.getMicroservice(targetService);
        ServerResponse res = service.executeCommand(req);
        sendResponse(res, event.getClientInterface(), event.getClientInterfaceState());
    }

    private void sendResponse(ServerResponse res, ClientCommunicationInterface clientInterface, Object clientInterfaceState)
    {
        clientInterface.sendReply(clientInterfaceState, res);
    }

}
