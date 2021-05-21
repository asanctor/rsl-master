package rsl.server.microservices.defaultservice;

import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslResource;
import rsl.core.coremodel.RslSelector;
import rsl.server.ClientRequest;
import rsl.server.ServerResponse;

import java.util.Set;

import static rsl.server.microservices.defaultservice.RequestProcessHelper.*;

@SuppressWarnings("Duplicates")
class ResourceMethods {

    static ServerResponse setHostedContent(ClientRequest req)
    {
        long id;
        RslEntity entity;
        RslResource resource;
        String hostedContentIndex;
        try {
            id = extractEntityID(req, "id");
            entity = getEntityByID(id);
            resource = castToResource(entity);
            hostedContentIndex = extractString(req, "hostedContentIndex");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        resource.setHostedContentIndex(hostedContentIndex);
        return new ServerResponse(200, "Set resource hosted content.", null);
    }

    static ServerResponse detachHostedContent(ClientRequest req)
    {
        long id;
        RslEntity entity;
        RslResource resource;
        try {
            id = extractEntityID(req, "id");
            entity = getEntityByID(id);
            resource = castToResource(entity);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        resource.detachHostedContent();
        return new ServerResponse(200, "Detached resource hosted content.", null);
    }

    static ServerResponse deleteHostedContent(ClientRequest req)
    {
        long id;
        RslEntity entity;
        RslResource resource;
        try {
            id = extractEntityID(req, "id");
            entity = getEntityByID(id);
            resource = castToResource(entity);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        resource.deleteHostedContent();
        return new ServerResponse(200, "Deleted resource hosted content.", null);
    }

    static ServerResponse getSelectors(ClientRequest req)
    {
        long id;
        RslEntity entity;
        RslResource resource;
        try {
            id = extractEntityID(req, "id");
            entity = getEntityByID(id);
            resource = castToResource(entity);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        Set<RslSelector> result = resource.getSelectors();
        return new ServerResponse(200, "Found selectors for resource with ID '" + id + "'", result);
    }

    static ServerResponse addSelector(ClientRequest req)
    {
        long id;
        RslEntity entity;
        RslResource resource;

        long selectorID;
        RslEntity entity2;
        RslSelector selector;

        try {
            id = extractEntityID(req, "id");
            entity = getEntityByID(id);
            resource = castToResource(entity);
            selectorID = extractEntityID(req, "selectorID");
            entity2 = getEntityByID(selectorID);
            selector = castToSelector(entity2);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        selector.setSelectorResource(resource);

        return new ServerResponse(200, "Set target resource of selector with ID '" + selectorID + "'", null);
    }

}
