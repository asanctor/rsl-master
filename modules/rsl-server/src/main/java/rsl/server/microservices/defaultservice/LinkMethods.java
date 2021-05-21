package rsl.server.microservices.defaultservice;

import rsl.core.RslQuery;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.server.ClientRequest;
import rsl.server.ServerResponse;
import rsl.util.Log;

import java.util.ArrayList;
import java.util.List;

import static rsl.server.microservices.defaultservice.RequestProcessHelper.*;

class LinkMethods {

    @SuppressWarnings("Duplicates")
    static ServerResponse addLinkSources(ClientRequest req)
    {

        long entityID;
        RslEntity entity;
        RslLink link;
        List<Number> sources;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            link = castToLink(entity);
            sources = extractNumberListParameter(req, "sources");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for(Number sourceID : sources)
        {
            RslEntity en =  RslQuery.getEntityByID(sourceID.longValue());
            if(en != null)
            {
                link.addSource(en);
            }
        }

        return new ServerResponse(200, "Added sources to link.", null);
    }

    @SuppressWarnings({"unchecked", "Duplicates"})
    static ServerResponse addLinkTargets(ClientRequest req)
    {

        long entityID;
        RslEntity entity;
        RslLink link;
        List<Number> targets;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            link = castToLink(entity);
            targets = extractNumberListParameter(req, "targets");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for(Number targetID : targets)
        {
            RslEntity en =  RslQuery.getEntityByID(targetID.longValue());
            if(en != null)
            {
                link.addTarget(en);
            }
        }

        return new ServerResponse(200, "Added targets to link.", null);
    }


    @SuppressWarnings({"unchecked", "Duplicates"})
    static ServerResponse removeLinkSources(ClientRequest req)
    {

        long entityID;
        RslEntity entity;
        RslLink link;
        List<Number> sources;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            link = castToLink(entity);
            sources = extractNumberListParameter(req, "sources");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for(Number sourceID : sources)
        {
            RslEntity en =  RslQuery.getEntityByID(sourceID.longValue());
            if(en != null)
            {
                link.removeSource(en);
            }
        }

        return new ServerResponse(200, "Removed sources from link.", null);

    }

    @SuppressWarnings({"unchecked", "Duplicates"})
    static ServerResponse removeLinkTargets(ClientRequest req)
    {

        long entityID;
        RslEntity entity;
        RslLink link;
        List<Number> targets;

        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            link = castToLink(entity);
            targets = extractNumberListParameter(req, "targets");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for(Number targetID : targets)
        {
            RslEntity en =  RslQuery.getEntityByID(targetID.longValue());
            if(en != null)
            {
                link.removeTarget(en);
            }
        }

        return new ServerResponse(200, "Removed sources from link.", null);

    }


}
