package rsl.server.microservices.defaultservice;

import rsl.core.RslQuery;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.core.coremodel.RslResource;
import rsl.core.coremodel.RslSelector;
import rsl.server.ClientRequest;
import rsl.server.ServerResponse;

import java.util.List;

import static rsl.server.microservices.defaultservice.RequestProcessHelper.*;

class SelectorMethods {

    static ServerResponse setSelectorResource(ClientRequest req)
    {
        long id;
        RslEntity entity;
        RslSelector selector;
        RslResource resource;

        try {
            id = extractEntityID(req, "selectorID");
            entity = getEntityByID(id);
            selector = castToSelector(entity);

            id = extractEntityID(req, "resourceID");
            entity = getEntityByID(id);
            resource = castToResource(entity);

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        selector.setSelectorResource(resource);

        return new ServerResponse(200, "Set selector resource.", null);
    }

    static ServerResponse getSelectorResource(ClientRequest req)
    {
        long id;
        RslEntity entity;
        RslSelector selector;

        try {
            id = extractEntityID(req, "id");
            entity = getEntityByID(id);
            selector = castToSelector(entity);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RslResource result = selector.getSelectorResource();
        return new ServerResponse(200, "Got selector resource.", result);
    }

}
