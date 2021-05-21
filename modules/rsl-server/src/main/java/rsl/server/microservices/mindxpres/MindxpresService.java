package rsl.server.microservices.mindxpres;

import rsl.server.ClientRequest;
import rsl.server.ServerResponse;
import rsl.server.microservices.Microservice;

public class MindxpresService extends Microservice {

    public MindxpresService()
    {
        super("MindXpres");
        super.registerCommand("getPresentation", this::getPresentation);
    }


    private ServerResponse getPresentation(ClientRequest req)
    {
        return null;
    }

}
