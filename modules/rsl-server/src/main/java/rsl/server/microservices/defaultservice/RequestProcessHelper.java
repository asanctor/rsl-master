package rsl.server.microservices.defaultservice;

import rsl.core.RslQuery;
import rsl.core.coremodel.*;
import rsl.server.ClientRequest;
import rsl.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestProcessHelper {

    public static long extractEntityID(ClientRequest req, String parameterName) throws RequestProcessingException
    {
        Integer entityID = req.getQueryParameter(parameterName, Integer.class);
        if(entityID == null) {
            throw new RequestProcessingException(400, "Invalid or missing entity ID");
        }else{
            return entityID;
        }
    }

    public static String extractEntityName(ClientRequest req, String parameterName) throws RequestProcessingException
    {
        String entityName = req.getQueryParameter(parameterName, String.class);
        if(entityName == null) {
            throw new RequestProcessingException(400, "Invalid or missing entity ID");
        }else{
            return entityName;
        }
    }

    //basically the same as previous method, but since users are no entities we differentiate them
    public static long extractUserID(ClientRequest req, String parameterName) throws RequestProcessingException
    {
        Integer userID = req.getQueryParameter(parameterName, Integer.class);
        if(userID == null) {
            throw new RequestProcessingException(400, "Invalid or missing user ID");
        }else{
            return userID;
        }
    }

    public static String extractString(ClientRequest req, String parameterName) throws RequestProcessingException
    {
        String result = req.getQueryParameter(parameterName, String.class);
        if(result == null) {
            throw new RequestProcessingException(400, "Invalid or missing request parameter '" + parameterName + "'");
        }else{
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Number> extractNumberListParameter(ClientRequest req, String valueName) throws RequestProcessingException
    {
        List<Number> result;
        try{
            result = req.getQueryParameter(valueName, List.class);
            if(result == null){ throw new Exception(); }
        }catch(Exception e){
            throw new RequestProcessingException(400, "Could not parse list '" + valueName + "'.");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static List<String> extractStringListParameter(ClientRequest req, String valueName) throws RequestProcessingException
    {
        List<String> result;
        try{
            result = req.getQueryParameter(valueName, List.class);
            if(result == null){ throw new Exception(); }
        }catch(Exception e){
            throw new RequestProcessingException(400, "Could not parse list '" + valueName + "'.");
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> extractMapParameter(ClientRequest req, String valueName) throws RequestProcessingException
    {
        Map<String, Object> result;
        try{
            result = req.getQueryParameter(valueName, HashMap.class);
            if(result == null){ throw new Exception(); }
        }catch(Exception e){
            throw new RequestProcessingException(400, "Could not parse list '" + valueName + "'.");
        }
        return result;
    }


    public static RslEntity getEntityByID(long id) throws RequestProcessingException
    {
        RslEntity result =  RslQuery.getEntityByID(id);
        if(result == null)
        {
            throw new RequestProcessingException(404, "No entity with ID '" + id + "'.");
        }else{
            return result;
        }
    }

    public static List getEntitiesByClass(String className) throws RequestProcessingException
    {
        List result =  RslQuery.getEntitiesByClass(className);
        if(result == null)
        {
            throw new RequestProcessingException(404, "No entity with class '" + className + "'.");
        }else{
            return result;
        }
    }

    static RslEntity getEntityByName(String name) throws RequestProcessingException
    {
        RslEntity result =  RslQuery.getEntityByName(name);
        if(result == null)
        {
            throw new RequestProcessingException(404, "No entity with name '" + name + "'.");
        }else{
            return result;
        }
    }

    public static RslUser getUserByID(long id) throws RequestProcessingException
    {
        RslUser result =  RslQuery.getUserByID(id);
        if(result == null)
        {
            throw new RequestProcessingException(404, "No user with ID '" + id + "'.");
        }else{
            return result;
        }
    }

    static RslUser getUserByDatabaseID(long id) throws RequestProcessingException
    {
        RslUser result =  RslQuery.getUserByDatabaseID(id);
        if(result == null)
        {
            throw new RequestProcessingException(404, "No user with ID '" + id + "'.");
        }else{
            return result;
        }
    }

    public static RslUser getUserByUsername(String username) throws RequestProcessingException
    {
        RslUser result =  RslQuery.getUserByUsername(username);
        if(result == null)
        {
            throw new RequestProcessingException(404, "No user with username '" + username + "'.");
        }else{
            return result;
        }
    }

    static RslLink castToLink(RslEntity entity) throws RequestProcessingException
    {
        if(entity instanceof RslLink){
            return (RslLink) entity;
        }else{
            throw new RequestProcessingException(400, "Specified ID does not represent an RSL link");
        }
    }

    static RslSelector castToSelector(RslEntity entity) throws RequestProcessingException
    {
        if(entity instanceof RslSelector){
            return (RslSelector) entity;
        }else{
            throw new RequestProcessingException(400, "Specified ID does not represent an RSL selector");
        }
    }

    static RslResource castToResource(RslEntity entity) throws RequestProcessingException
    {
        if(entity instanceof RslResource){
            return (RslResource) entity;
        }else{
            throw new RequestProcessingException(400, "Specified ID does not represent an RSL resource");
        }
    }

    //used to cast a user to a group (not sure needed yet)
    static RslGroup castToGroup(RslUser group) throws RequestProcessingException
    {
        if(group instanceof RslGroup){
            return (RslGroup) group;
        }else{
            throw new RequestProcessingException(400, "Specified ID does not represent an RSL Group");
        }
    }
}
