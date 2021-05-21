package rsl.server.microservices.defaultservice;

import rsl.server.ClientRequest;
import rsl.server.HTTPStoredContentHoster;
import rsl.server.RSLServer;
import rsl.server.ServerResponse;
import rsl.server.microservices.Microservice;
import rsl.server.microservices.MicroserviceManager;
import java.util.HashMap;

public class DefaultService extends Microservice {


    public DefaultService()
    {
        super("default");

        // some server-related commands
        super.registerCommand("getServerVersion", this::getServerVersion);
        super.registerCommand("getServiceInfo", this::getServiceInfo);
        super.registerCommand("getContentHosterPort", this::getContentHosterPort);

        // some generic methods for working with entities
        super.registerCommand("createEntity", EntityMethods::createEntity);
        super.registerCommand("deleteEntity", EntityMethods::deleteEntity);
        super.registerCommand("setEntityFields", EntityMethods::setEntityFields);
        super.registerCommand("getEntity", EntityMethods::getEntity);
        super.registerCommand("getEntityByName", EntityMethods::getEntityWithName);
        super.registerCommand("getEntities", EntityMethods::getEntities);
        super.registerCommand("getEntityClass", EntityMethods::getEntityClass);
        super.registerCommand("getEntitiesClass", EntityMethods::getEntitiesClass);
        super.registerCommand("getEntitiesOfClass", EntityMethods::getEntitiesOfClass);

        // some generic methods for working with links
        super.registerCommand("addLinkSources", LinkMethods::addLinkSources);
        super.registerCommand("addLinkTargets", LinkMethods::addLinkTargets);
        super.registerCommand("removeLinkSources", LinkMethods::removeLinkSources);
        super.registerCommand("removeLinkTargets", LinkMethods::removeLinkTargets);

        // some generic methods for working with selectors
        super.registerCommand("setSelectorResource", SelectorMethods::setSelectorResource);
        super.registerCommand("getSelectorResource", SelectorMethods::getSelectorResource);

        // some generic methods for working with resources
        super.registerCommand("setHostedContent", ResourceMethods::setHostedContent);
        super.registerCommand("detachHostedContent", ResourceMethods::detachHostedContent);
        super.registerCommand("deleteHostedContent", ResourceMethods::deleteHostedContent);
        super.registerCommand("getSelectors", ResourceMethods::getSelectors);
        super.registerCommand("addSelector", ResourceMethods::addSelector);

        // some generic methods for working with users
        super.registerCommand("getUser", UserMethods::getUser);
        super.registerCommand("getUsers", UserMethods::getUsers);
        super.registerCommand("getAllUsers", UserMethods::getAllUsers);
        super.registerCommand("createUser", UserMethods::createUser);
        super.registerCommand("createGroup", UserMethods::createGroup);
        super.registerCommand("deleteUser", UserMethods::deleteUser);
        //super.registerCommand("getGroup", UserMethods::getGroup);
        //super.registerCommand("setGroup", UserMethods::setGroup);
        super.registerCommand("getCreatedEntities", UserMethods::getCreatedEntities);
        super.registerCommand("getCreatedEntitiesByDbId", UserMethods::getCreatedEntitiesByDbId);
        super.registerCommand("getAccessibleEntities", UserMethods::getAccessibleEntities);
        super.registerCommand("getAccessibleEntitiesByType", UserMethods::getAccessibleEntitiesByType);
        super.registerCommand("addAccessibleEntities", UserMethods::addAccessibleEntities);
        super.registerCommand("addCreatedEntities", UserMethods::addCreatedEntities);

    }

    private ServerResponse getContentHosterPort(ClientRequest req)
    {
        int port = HTTPStoredContentHoster.port;
        return new ServerResponse(200, "ok", port);
    }

    private ServerResponse getServerVersion(ClientRequest req)
    {
        String version = RSLServer.getVersion();
        return new ServerResponse(200, "ok", version);
    }

    private ServerResponse getServiceInfo(ClientRequest req)
    {
        HashMap<String, String[]> result = new HashMap<>();
        String[] serviceNames = MicroserviceManager.getRegisteredServices();
        for(String serviceName: serviceNames)
        {
            Microservice service = MicroserviceManager.getMicroservice(serviceName);
            String[] serviceCommands = service.getCommandList();
            result.put(serviceName, serviceCommands);
        }
        return new ServerResponse(200, "ok", result);
    }








}