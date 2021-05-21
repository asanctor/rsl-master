package rsl.server.microservices.defaultservice;

import com.toddfast.util.convert.TypeConverter;
import org.apache.commons.lang3.reflect.MethodUtils;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.RslQuery;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.server.ClientRequest;
import rsl.server.ServerResponse;
import rsl.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rsl.server.microservices.defaultservice.RequestProcessHelper.*;

class EntityMethods {

    private static final Set<Class<?>> primitiveNumbers = Stream
            .of(int.class, long.class, float.class,
                    double.class, byte.class, short.class)
            .collect(Collectors.toSet());

    static ServerResponse getEntity(ClientRequest req) {
        long entityID;
        RslEntity entity;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RSL.getDB().refresh(entity);
        return new ServerResponse(200, "Found entity with ID '" + entityID + "'", entity);

    }

    static ServerResponse getEntityClass(ClientRequest req) {
        long entityID;
        RslEntity entity;
        Class c = null;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            c = entity.getClass();

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RSL.getDB().refresh(entity);
        return new ServerResponse(200, "Found entity with class '" + c.toString() + "'", c.toString());

    }

    static ServerResponse getEntitiesClass(ClientRequest req) {
        List<Number> entityIDs;
        Class c;
        try {
            entityIDs = extractNumberListParameter(req, "entities");

        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        List<String> entityClasses = new LinkedList<>();
        for(Number id : entityIDs)
        {
            long eID = id.longValue();
            RslEntity entity = RslQuery.getEntityByID(eID);
            c = entity.getClass();
            entityClasses.add(c.toString());

            RSL.getDB().refresh(entity);
        }

        return new ServerResponse(200, "Found entity classes.", entityClasses);

    }

    static ServerResponse getEntityWithName(ClientRequest req)
    {
        String entityName;
        RslEntity entity;
        try {
            entityName = extractEntityName(req, "name");
            entity = getEntityByName(entityName);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RSL.getDB().refresh(entity);
        return new ServerResponse(200, "Found entity with name '" + entityName + "'", entity);

    }

    static ServerResponse getEntitiesOfClass(ClientRequest req)
    {
        String entityClass;
        List entity;
        try {
            entityClass = extractEntityName(req, "class");
            entity = getEntitiesByClass(entityClass);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RSL.getDB().refresh(entity);
        return new ServerResponse(200, "Found entity with class '" + entityClass + "'", entity);

    }


    static ServerResponse getEntities(ClientRequest req)
    {

        List<Number> entityIDs;
        try {
            entityIDs = extractNumberListParameter(req, "entities");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        List<RslEntity> entities = new LinkedList<>();
        for(Number id : entityIDs)
        {
            long eID = id.longValue();
            entities.add(RslQuery.getEntityByID(eID));
        }

        return new ServerResponse(200, "Retrieved entities.", entities);

    }



    static ServerResponse deleteEntity(ClientRequest req)
    {

        long entityID;
        RslEntity entity;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }

        RSL.getDB().delete(entity);
        return new ServerResponse(200, "Deleted entity with ID '" + entityID + "'", null);

    }

    static ServerResponse createEntity(ClientRequest req) {

        String modelName = req.getQueryParameter("model", String.class);
        String className = req.getQueryParameter("entity", String.class);

        if(modelName == null || className == null){
            return new ServerResponse(400, "Invalid request, model and entity names need to be provided.", null);
        }

        RslEntity entity = EntityFactory.createResource(modelName, className);
        if(entity == null){entity = EntityFactory.createSelector(modelName, className);}
        if(entity == null){entity = EntityFactory.createLink(modelName, className);}

        if(entity != null)
        {
            entity.save();
            String msg = String.format("Created entity with name '%s' from model '%s'.", className, modelName);
            return new ServerResponse(200, msg, entity);
        } else {
            String msg = String.format("Entity with name '%s' in model '%s' does not exist.", className, modelName);
            return new ServerResponse(500, msg, null);
        }

    }

    @SuppressWarnings("unchecked")
    static ServerResponse setEntityFields(ClientRequest req)
    {

        long entityID;
        RslEntity entity;
        Map<String, Object> fields;
        try {
            entityID = extractEntityID(req, "id");
            entity = getEntityByID(entityID);
            fields = extractMapParameter(req, "fields");
        }catch(RequestProcessingException e) {
            return new ServerResponse(e.getStatusCode(), e.getMessage(), null);
        }


        for(Map.Entry<String, Object> entry :  fields.entrySet()){
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            if(fieldName == null || fieldName.equals("") || fieldValue == null){
                continue;
            }

            String methodName = "set" + capitalizeFirstLetter(fieldName);

            Method[] methods = entity.getClass().getMethods();
            for(Method method : methods)
            {
                if(method.getName().equals(methodName))
                {
                    Parameter[] params = method.getParameters();
                    if(params.length == 1)
                    {
                        Parameter targetParam = params[0];
                        try {
                            System.out.println(targetParam.getType().toString() + fieldValue + "where it went wrong");
                            method.invoke(entity, convertObject(targetParam.getType(), fieldValue));
                            break;
                        } catch(Exception e) {
                            e.printStackTrace();
                            return new ServerResponse(500, "Could not set value '" + fieldName + "'.", null);
                        }
                    }
                }
            }
        }

        return new ServerResponse(200, "Field values set for entity with ID '" + entity.getID() + "'.", entity);

    }

    @SuppressWarnings("unchecked")
    private static <T> T convertObject(Class<T> type, Object value) {
        T castedObject = null;

        // We need to transform a number to a number
        if(isNumericType(type) && isNumericType(value.getClass())){
            // hacky solution to bypass the Long/Double/Integer casting rules in Java
            if(type == short.class || type == long.class || type == int.class) {
                // cut off the decimal part so that it can be parsed by Java
                String strNum = value.toString();
                String[] numPieces = strNum.split("\\.");
                strNum = numPieces[0];
                castedObject = TypeConverter.convert(type, strNum);
            } else {
                try{
                    castedObject = TypeConverter.convert(type, value);
                }catch(Exception e){}
            }
        } else if(value.getClass().isAssignableFrom(type)) {
            castedObject = ((T) value);
        } else {
            try {
                castedObject = TypeConverter.convert(type, value);
            } catch(Exception e) { }
        }
        return castedObject;
    }

    private static boolean isNumericType(Class<?> cls) {
        if (cls.isPrimitive()) {
            return primitiveNumbers.contains(cls);
        } else {
            return Number.class.isAssignableFrom(cls);
        }
    }

    private static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

}
