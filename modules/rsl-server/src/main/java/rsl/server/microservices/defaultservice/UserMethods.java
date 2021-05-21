package rsl.server.microservices.defaultservice;

import rsl.core.RSL;
import rsl.core.RslQuery;
import rsl.core.coremodel.*;
import rsl.server.ClientRequest;
import rsl.server.ServerResponse;

import java.util.*;

import static rsl.server.ServerResponse.STATUS_NOT_FOUND;
import static rsl.server.microservices.defaultservice.RequestProcessHelper.*;

public class UserMethods {
    public UserMethods() {
    }

    static ServerResponse getUser(ClientRequest req)
    {
        long userID;
        RslUser user;
        try {
            userID = extractUserID(req, "id");
            user = getUserByID(userID);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RSL.getDB().refresh(user);
        return new ServerResponse(200, "Found user with ID '" + userID + "'", user);

    }


    static ServerResponse getUsers(ClientRequest req)
    {

        List<Number> userIDs;
        try {
            userIDs = extractNumberListParameter(req, "users");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        List<RslUser> users = new LinkedList<>();
        for(Number id : userIDs)
        {
            long eID = id.longValue();
            users.add(RslQuery.getUserByID(eID));
        }

        return new ServerResponse(200, "Retrieved users.", users);
    }

    static ServerResponse getAllUsers(ClientRequest req)
    {

        List<RslUser> users = RslQuery.getUsers();

        return new ServerResponse(200, "Retrieved users.", users);
    }


    static ServerResponse deleteUser(ClientRequest req)
    {

        long userID;
        RslUser user;
        try {
            userID = extractUserID(req, "id");
            user = getUserByID(userID);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RSL.getDB().delete(user);
        return new ServerResponse(200, "Deleted user with ID '" + userID + "'", null);

    }

    static ServerResponse createUser(ClientRequest req) {

        //TODO: not sure needed, uncomment if needed
        //String modelName = req.getQueryParameter("model", String.class);

        String userName = req.getQueryParameter("name", String.class);

        //make sure the user has a name
        if(userName == null){
            return new ServerResponse(400, "Invalid request, user name need to be provided.", null);
        }

        //use true to save it to the db
        RslUser user = new RslUser(true);
        user.setUsername(userName);
        //not sure needed, maybe it updates automatically
        user.save();

        String msg = String.format("Created user with name '%s'.", userName);
        return new ServerResponse(200, msg, user);

        /* I don't think this is needed:
        if(user != null){
            String msg = String.format("User with name '%s' could not be created.", userName);
            return new ServerResponse(500, msg, null);
        }*/

    }

    static ServerResponse createGroup(ClientRequest req) {

        //TODO: not sure needed, uncomment if needed
        //String modelName = req.getQueryParameter("model", String.class);

        String name = req.getQueryParameter("name", String.class);

        //make sure the user has a name
        if(name == null){
            return new ServerResponse(400, "Invalid request, group name need to be provided.", null);
        }

        //use true to save it to the db
        RslGroup group = new RslGroup(true);
        group.setName(name);
        //not sure needed, maybe it updates automatically
        group.save();

        String msg = String.format("Created group with name '%s'.", name);
        return new ServerResponse(200, msg, group);

    }

    static ServerResponse getCreatedEntities(ClientRequest req) {

        long id;
        RslUser user;
        Set<RslEntity> entities;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        entities = user.getCreatedEntities();
        return new ServerResponse(200, "Retrieved created entities of user '" + id + "'.", entities);

    }


    static ServerResponse getCreatedEntitiesByDbId(ClientRequest req) {

        long id;
        RslUser user;
        Set<RslEntity> entities;
        try {
            id = extractUserID(req, "dbId");
            user = getUserByDatabaseID(id);

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        entities = user.getCreatedEntities();
        return new ServerResponse(200, "Retrieved created entities of user '" + id + "'.", entities);

    }

    @SuppressWarnings("Duplicates")
    static ServerResponse addCreatedEntities(ClientRequest req) {

        long id;
        RslUser user;
        List<Number> entities;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);
            entities = extractNumberListParameter(req, "entities");
            System.out.println("these are the entities: " + entities.toString());

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for(Number entityID : entities)
        {
            RslEntity en =  RslQuery.getEntityByID(entityID.longValue());
            System.out.println("this is the entity: " + en.toString());
            if(en != null)
            {
                user.addCreatedEntity(en);
            }
        }
        RSL.getDB().refresh(user);
        return new ServerResponse(200, "Added created entities to user.", null);

    }


    static ServerResponse getAccessibleEntities(ClientRequest req) {

        long id;
        RslUser user;
        Set<RslEntity> entities;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        if(user != null){
            entities = user.getAccessibleEntities();
            return new ServerResponse(200, "Retrieved accessible entities of user '" + id + "'.", entities);
        } else {
            return new ServerResponse(404, "User with id '" + id + "' could not be found.", id);
        }


    }

    static ServerResponse getAccessibleEntitiesByType(ClientRequest req) {
        long userID;
        String type = "";
        RslUser user;
        Set<RslEntity> entitiesOfType = new HashSet<>();
        try {
            userID = extractUserID(req, "id");
            type = extractString(req, "type");
            user = getUserByID(userID);
            Class c = Class.forName("rsl.models.espace.resources." + type);
            entitiesOfType = user.getAccessibleEntitiesOfType(c);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        } catch (ClassNotFoundException e) {
            return new ServerResponse(STATUS_NOT_FOUND, "class of type '" + type + "' not found", null);
        }

        RSL.getDB().refresh(user);
        return new ServerResponse(200, "Entities of type '" + type + "' from user with ID '" + userID + "'", entitiesOfType);

    }

    @SuppressWarnings("Duplicates")
    static ServerResponse addAccessibleEntities(ClientRequest req) {

        long id;
        RslUser user;
        List<Number> entities;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);
            entities = extractNumberListParameter(req, "entities");

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for(Number entityID : entities)
        {
            RslEntity en =  RslQuery.getEntityByID(entityID.longValue());

            if(en != null)
            {
                user.addAccessibleEntity(en);

            }
        }

        return new ServerResponse(200, "Added accessible entities to user.", null);

    }


    //returns list of pairs of the edited entity and the timestamp of when they have been edited
    static ServerResponse getEditedEntities(ClientRequest req) {

        long id;
        RslUser user;
        Set<EntityEditorTimestamp> entities;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        entities = user.getEditedEntities();
        return new ServerResponse(200, "Retrieved edited entities of user '" + id + "' together with timestamps.", entities);

    }

    //expects a list containing a pair of entity id and a timestamp
    @SuppressWarnings("unchecked")
    static ServerResponse addEditedEntities(ClientRequest req) {

        long id;
        RslUser user;
        Map<Number, Number> entities;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);
            entities = req.getQueryParameter("entities", HashMap.class);

        } catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for (Map.Entry<Number, Number> entry : entities.entrySet()) {
            long entityID = entry.getKey().longValue();
            long timestamp = entry.getValue().longValue();
            RslEntity en =  RslQuery.getEntityByID(entityID);
            if(en != null)
            {
                EntityEditorTimestamp editedEntity = new EntityEditorTimestamp(true);
                editedEntity.setEntity(en);
                editedEntity.setEditor(user);
                editedEntity.setEditTimestamp(timestamp);
                user.addEditedEntity(editedEntity);
            }

        }

        return new ServerResponse(200, "Added edited entities to user.'" + id + "' together with timestamps.", null);

    }


    static ServerResponse getPreferences(ClientRequest req) {

        long id;
        RslUser user;
        Set<Preference> preferences;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        preferences = user.getPreferences();
        return new ServerResponse(200, "Retrieved preferences of user '" + id + "'.", preferences);

    }

    @SuppressWarnings("unchecked, Duplicates")
    static ServerResponse addPreferences(ClientRequest req) {

        long id;
        RslUser user;
        Map<String, String> preferences;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);
            preferences = req.getQueryParameter("preferences", HashMap.class);

        } catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            String preferenceName = entry.getKey();
            String preferenceValue = entry.getValue();

            Preference pr = new Preference(preferenceName, preferenceValue, true);

            user.addPreferencePair(pr);
        }
        return new ServerResponse(200, "Added preferences to user '" + id + "'.", null);
    }

    @SuppressWarnings("unchecked, Duplicates")
    static ServerResponse removePreferences(ClientRequest req) {

        long id;
        RslUser user;
        Map<String, String> preferences;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);
            preferences = req.getQueryParameter("preferences", HashMap.class);

        } catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for (Map.Entry<String, String> entry : preferences.entrySet()) {
            String preferenceName = entry.getKey();
            String preferenceValue = entry.getValue();
            Preference pr = new Preference(preferenceName, preferenceValue, true);

            user.removePreferencePair(pr);
            //TODO: also need to remove from db? or automatic?
        }
        return new ServerResponse(200, "Preference pairs removed from user '" + id + "'.", null);
    }


    static ServerResponse getGroups(ClientRequest req) {

        long id;
        RslUser user;
        Set<RslGroup> groups;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);

        } catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        groups = user.getGroups();
        return new ServerResponse(200, "Retrieved groups of user '" + id + "'.", groups);

    }

    static ServerResponse removeFromGroups(ClientRequest req) {

        long id;
        RslUser user;
        List<Number> groups;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);
            groups = extractNumberListParameter(req, "groups");

            for(Number groupID : groups) {

                RslUser u = getUserByID(groupID.longValue());
                RslGroup gr = castToGroup(u);

                user.removeGroup(gr);
                //TODO: also need to remove from db? or automatic?
            }

        } catch(RequestProcessingException e) {
                return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }
        
        return new ServerResponse(200, "Removed user '" + id + "' from some groups.", null);
    }

    static ServerResponse addToGroups(ClientRequest req) {

        long id;
        RslUser user;
        List<Number> groups;
        try {
            id = extractUserID(req, "id");
            user = getUserByID(id);
            groups = extractNumberListParameter(req, "groups");

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        for(Number groupID : groups) {
            try{
                RslUser u = RslQuery.getUserByID(groupID.longValue());
                RslGroup gr = castToGroup(u);
                user.addGroup(gr);

            } catch(RequestProcessingException e) {
                return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
            }

        }

        return new ServerResponse(200, "Added user '" + id + "' to the groups.", null);
    }






}
