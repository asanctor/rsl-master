package rsl.core;

import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.core.coremodel.RslResource;
import rsl.core.coremodel.RslSelector;
import rsl.util.Log;

import java.io.IOException;

public class EntityFactory {

    public static <T extends RslResource> T createResource(String modelName, String className)
    {
        return EntityFactory.createEntityInstance(modelName, className, "resources", RslResource.class);
    }

    public static <T extends RslSelector> T createSelector(String modelName, String className)
    {
        return EntityFactory.createEntityInstance(modelName, className, "selectors", RslSelector.class);
    }

    public static <T extends RslLink> T createLink(String modelName, String className)
    {
        return EntityFactory.createEntityInstance(modelName, className, "links", RslLink.class);
    }

    @SuppressWarnings("unchecked")
    private static <T, U extends T> U createEntityInstance(String modelName, String className, String packagePrefix, Class<T> returnType)
    {
        Class<?> c = getEntityClass(modelName, className, packagePrefix, returnType);
        if(c != null) {
            try {
                return (U) c.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Log.warning("Could not create class instance!");
                return null;
            }
        } else {
            return null;
        }
    }


    public static Class<? extends RslResource> getResourceClass(String modelName, String className)
    {
        return EntityFactory.getEntityClass(modelName, className, "resources", RslResource.class);
    }

    public static Class<? extends RslSelector> getSelectorClass(String modelName, String className)
    {
        return EntityFactory.getEntityClass(modelName, className, "selectors", RslSelector.class);
    }

    public static Class<? extends RslLink> getLinkClass(String modelName, String className)
    {
        return EntityFactory.getEntityClass(modelName, className, "links", RslLink.class);
    }

    @SuppressWarnings("unchecked")
    private static <T, U extends T> Class<U> getEntityClass(String modelName, String className, String packagePrefix, Class<T> superType)
    {
        String fullName = "rsl.models." + modelName.toLowerCase() + "." + packagePrefix + "." + className;
        try {
            return (Class<U>) Class.forName(fullName);
        } catch (ClassNotFoundException e) {
            Log.warning("Class not found: " + fullName);
            return null;
        }
    }

}
