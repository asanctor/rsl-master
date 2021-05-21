package rsl.persistence;

import rsl.util.Log;

import java.io.File;
import java.io.IOException;

class ObjectDBRslPersistence extends RslPersistence {

    static {
        /*
        String classesToEnhance = "rsl.core.coremodel.*";
        AgentLoader.loadAgent(PersistenceProviders.getProviderJARPath(), "");
        try {
            Class<?> c = Class.forName("com.objectdb.Enhancer");
            Method m = c.getMethod("enhance", String.class, ClassLoader.class);
            m.invoke(c, classesToEnhance, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            Log.warning("Enhancer class not found!");
        } catch (NoSuchMethodException e) {
            Log.warning("Enhancer method not found!");
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.warning("Could not invoke enhancer method!");
        }
        */
    }

    public ObjectDBRslPersistence(String connectionString)
    {
        super(connectionString);
    }


    @Override
    public void enhanceJARs(String[] paths)
    {

        /*
            String args = paths[0];
            try {
                Class<?> c = Class.forName("com.objectdb.Enhancer");
                Method m = c.getMethod("enhance", String.class);
                m.invoke(c, args);
                Log.debug(c.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            System.setOut(originalStream);
        */


        // the classpath seperator is different depending on your OS! (":" for unix/macOS, ";" for windows, ...)
        String classPathSeparator = System.getProperties().getProperty("path.separator");
        String[] classPathParts = {"*",  "libs/*", "persistence/*"};
        String classPath = String.join(classPathSeparator, classPathParts);

        System.out.println(classPath);

        ProcessBuilder pb = new ProcessBuilder("java", "-cp", classPath, "com.objectdb.Enhancer", paths[0]);
        pb.inheritIO(); // uncomment this to show process output in this app's stdout
        pb.directory(new File(System.getProperty("user.dir")));
        try {
            Process p = pb.start();     // Start the process.
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
