package rsl.core;

import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslGroup;
import rsl.core.coremodel.RslUser;
import rsl.core.coremodel.SecureToken;
import rsl.persistence.PersistenceQuery;

import java.util.List;

public class RslQuery {

    public static RslEntity getEntityByDatabaseID(long id)
    {
        return getEntityByDatabaseID(RslEntity.class, id);
    }

    public static <T extends RslEntity> T getEntityByDatabaseID(Class<T> entityClass, long id)
    {
        return RSL.getDB().getByDatabaseID(entityClass, id);
    }

    public static RslEntity getEntityByID(long id)
    {
        return getEntityByID(RslEntity.class, id);
    }

    public static RslEntity getEntityByName(String name)
    {
        return getEntityByName(RslEntity.class, name);
    }

    @SuppressWarnings("unchecked")
    public static <T extends RslEntity> T getEntityByID(Class<T> entityClass, long id)
    {
        RslEntity entity = RSL.getDB().getByID(entityClass, id);
        if(entity != null)
        {
            entity.refresh();
        }
        return (T)entity;
    }

    @SuppressWarnings("unchecked")
    public static <T extends RslEntity> T getEntityByName(Class<T> entityClass, String name)
    {
        RslEntity entity = RSL.getDB().getByName(entityClass, name);
        if(entity != null)
        {
            entity.refresh();
        }
        return (T)entity;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getEntitiesByClass(String className)
    {
        Class<T> c = (Class<T>) RslEntity.class; //set default to rslEntity class
        try {
            c = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<T> entities = RSL.getDB().getEntitiesByClass(c);

        return entities;
    }

    //only username is unique so the same email could be assigned to multiple users
    @SuppressWarnings("unchecked")
    public static List<RslUser> getUsersByEmail(String email) {

        List<RslUser> users = RSL.getDB().getUsersByEmail(RslUser.class, email);

        return users;
    }

    public static void deleteEntityByDatabaseID(long id)
    {
        RslEntity entity = RSL.getDB().getByDatabaseID(RslEntity.class, id);
        if(entity != null)
        {
            RSL.getDB().delete(entity);
        }
    }

    public static void deleteEntityByID(long id)
    {
        RslEntity entity = RSL.getDB().getByID(RslEntity.class, id);
        if(entity != null)
        {
            RSL.getDB().delete(entity);
        }
    }

    public static <T extends RslEntity> void deleteEntity(T entity)
    {
        RSL.getDB().delete(entity);
    }

    public static <T extends SecureToken> void deleteToken(T token)
    {
        RSL.getDB().delete(token);
    }

    public static List executeQuery(PersistenceQuery query)
    {
        return RSL.getDB().executeQuery(query);
    }

    public static long countDatabaseObjects()
    {
        return RSL.getDB().countAll();
    }

    // deleted "extends RslEntity" since otherwise users would not be included, TODO: not sure if correct
    public static <T> long countDatabaseObjectsByClass(Class<T> c)
    {
        return RSL.getDB().countByClass(c);
    }

    /*
    ** same for users TODO: not sure needed, since lots of duplicated code :/
    */

    public static RslUser getUserByDatabaseID(long id)
    {
        return getUserByDatabaseID(RslUser.class, id);
    }

    public static <T extends RslUser> T getUserByDatabaseID(Class<T> userClass, long id)
    {
        return RSL.getDB().getByDatabaseID(userClass, id);
    }

    public static RslUser getUserByID(long id)
    {
        return getUserByID(RslUser.class, id);
    }

    public static RslUser getUserByUsername(String username)
    {
        return getUserByUsername(RslUser.class, username);
    }

    public static RslGroup getGroupByCode(String code)
    {
        return getGroupByCode(RslGroup.class, code);
    }

    public static SecureToken getTokenByToken(String token)
    {
        return getTokenByToken(SecureToken.class, token);
    }

    public static List<RslUser> getUsers()
    {
        Class<RslUser> c = RslUser.class; //set default to rslEntity class
        return RSL.getDB().getEntitiesByClass(c);
    }

    @SuppressWarnings("unchecked")
    public static <T extends RslUser> T getUserByID(Class<T> userClass, long id)
    {
        RslUser user = RSL.getDB().getByID(userClass, id);
        if(user != null)
        {
            user.refresh();
        }
        return (T)user;
    }

    @SuppressWarnings("unchecked")
    public static <T extends RslUser> T getUserByUsername(Class<T> userClass, String username)
    {
        RslUser user = RSL.getDB().getByUsername(userClass, username);
        if(user != null)
        {
            user.refresh();
        }
        return (T)user;
    }

    @SuppressWarnings("unchecked")
    public static <T extends RslGroup> T getGroupByCode(Class<T> groupClass, String code)
    {
        RslGroup group = RSL.getDB().getByCode(groupClass, code);
        if(group != null)
        {
            group.refresh();
        }
        return (T)group;
    }

    //not sure this is very clean but at least it works
    @SuppressWarnings("unchecked")
    public static <T extends SecureToken> T getTokenByToken(Class<T> userClass, String token)
    {
        SecureToken sToken = RSL.getDB().getByToken(userClass, token);
        if(sToken != null)
        {
            sToken.refresh();
        }
        return (T)sToken;
    }

    public static void deleteUserByDatabaseID(long id)
    {
        RslUser user = RSL.getDB().getByDatabaseID(RslUser.class, id);
        if(user != null)
        {
            RSL.getDB().delete(user);
        }
    }

    public static void deleteUserByID(long id)
    {
        RslUser user = RSL.getDB().getByID(RslUser.class, id);
        if(user != null)
        {
            RSL.getDB().delete(user);
        }
    }

    public static <T extends RslUser> void deleteUser(T user)
    {
        RSL.getDB().delete(user);
    }


}
