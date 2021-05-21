package rsl.util;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author rroels
 *
 * Utility class for helping with classpath related things such as listing all loaded classes, or getting all classes for a specific package.
 */

public class ClassPath {


    public static void logLoadedClasses(String packageName)
    {
        logLoadedClasses(packageName, null);
    }

    public static void logLoadedClasses(String packageName, ClassLoader cl)
    {
        String classes[] = getClassesByPackage(packageName);
        for(String c: classes)
        {
            Log.debug(c);
        }
    }

    public static String[] getClassesByPackage(String packageName)
    {
        return getClassesByPackage(packageName, null);
    }

    public static String[] getClassesByPackage(String packageName, ClassLoader cl)
    {
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());
        if(cl != null){
            classLoadersList.add(cl);
        }

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[classLoadersList.size()]))));


        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        return allClasses.stream().map(Class::getName).filter(n -> n.contains(packageName)).toArray(size -> new String[size]);
    }


}
