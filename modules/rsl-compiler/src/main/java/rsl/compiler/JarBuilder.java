package rsl.compiler;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author rroels
 *
 * The JarBuilder class creates a valid Java JAR archive out of a collection of MemoryJavaClassFile files
 */
public class JarBuilder {

    public static void createJar(MemoryJavaClassFile[] files, String jarFileName)
    {
        //prepare Manifest file
        String manifestVersion = "1.0";
        String implementationVersion = "0.0.1";
        String author = "rsl";
        Manifest manifest = new Manifest();
        Attributes global = manifest.getMainAttributes();
        global.put(Attributes.Name.MANIFEST_VERSION, manifestVersion);
        global.put(Attributes.Name.IMPLEMENTATION_VERSION, implementationVersion);
        global.put(new Attributes.Name("Created-By"), author);

        JarOutputStream jos = null;

        try {

            File jarFile = new File(jarFileName);
            OutputStream os = FileUtils.openOutputStream(jarFile);
            jos = new JarOutputStream(os, manifest);

            //start writing in jar
            int len;
            byte[] buffer = new byte[1024];
            for(MemoryJavaClassFile file: files ){
                String jarEntryPath = file.getClassName().replace(".", "/");
                JarEntry je = new JarEntry(jarEntryPath + ".class");
                jos.putNextEntry(je);
                InputStream is = new BufferedInputStream(file.openInputStream());
                while((len = is.read(buffer, 0, buffer.length)) != -1) {
                    jos.write(buffer, 0, len);
                }
                is.close();
                jos.closeEntry();
            }
            jos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
