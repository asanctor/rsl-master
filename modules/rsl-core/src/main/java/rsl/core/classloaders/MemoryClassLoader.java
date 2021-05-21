package rsl.core.classloaders;

import org.reflections.util.ClasspathHelper;
import rsl.compiler.MemoryJavaClassFile;
import rsl.util.ClassPath;
import rsl.util.Log;
import rsl.util.StackTrace;

import java.util.*;

// http://www.soulmachine.me/blog/2015/07/22/compile-and-run-java-source-code-in-memory/
// https://gist.github.com/chrisvest/9873843

/**
 * @author rroels
 *
 * The MemoryClassLoader is a modified ClassLoader that can load compiled Java classes directly into memory and onto
 * the classpath (at runtime). It was modified to load classes as MemoryJavaClassFiles instead of using the filesystem.
 * This way classes produced by the RSL compiler can be loaded directly into memory, bypassing the filesystem.
 */
public class MemoryClassLoader extends ClassLoader {

    private final HashMap<String, MemoryJavaClassFile> classes = new HashMap<>();
    private final HashMap<String, Class<?>> cache = new HashMap<>();
    private final HashMap<String, Boolean> loading = new HashMap<>();


    private int counter = 0;

    public MemoryClassLoader(List<MemoryJavaClassFile> classList, ClassLoader parent)
    {
        super(parent);
        for(MemoryJavaClassFile javaClass: classList)
        {
            this.classes.put(javaClass.getClassName(), javaClass);
        }
    }

    public void loadAll() throws Exception
    {
        Iterator it = classes.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            loadClass(key);

            //it.remove();
        }
    }

    public String[] getFullClassNames()
    {
        ArrayList<String> classNames = new ArrayList<>();
        classes.forEach((name, memFile) -> classNames.add(name));
        return classNames.toArray(new String[classes.size()]);
    }



    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryJavaClassFile javaClass = classes.getOrDefault(name, null);
        if(javaClass != null)
        {
            byte[] bytes = javaClass.getBytes();
            if(name.contains("ImageSelector")){
                Log.debug("DEFINEL: " + name);
            }
            return super.defineClass(name, bytes, 0, bytes.length);
        } else {
            return super.findClass(name);
        }
    }

/*
    private Class<?> getClass(String name) {
        MemoryJavaClassFile javaClass = classes.getOrDefault(name, null);
        if(javaClass != null)
        {
            byte[] bytes = javaClass.getBytes();
            Class<?> c = null;
            try {
                Log.debug("defining: " + name);
                c = super.defineClass(name, bytes, 0, bytes.length);
            } catch( LinkageError e)
            {

            }
            return c;
        } else {
            return null;
        }
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        Class<?> c;

        c = findLoadedClass(name);
        if(c != null) {
            return c;
        }

        try {
            return getSystemClassLoader().loadClass(name);
        } catch(ClassNotFoundException e) {
            Log.debug("customdefine: " + name);
            return getClass(name);
        }

    }
*/


}