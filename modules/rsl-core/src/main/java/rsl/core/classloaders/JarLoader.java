package rsl.core.classloaders;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author rroels
 *
 * The JarLoader is a class that loads all class files in a JAR file into memory and onto the classpath (at runtime).
 */
public class JarLoader
{

    // http://stackoverflow.com/questions/60764/how-should-i-load-jars-dynamically-at-runtime
    public static boolean loadJar(String path)
    {
        try {
            File file = new File(path);
            URL url = file.toURI().toURL();
            URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
