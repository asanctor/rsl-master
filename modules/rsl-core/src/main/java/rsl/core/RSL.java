package rsl.core;

import com.ea.agentloader.AgentLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import rsl.compiler.CompiledModel;
import rsl.compiler.JarBuilder;
import rsl.compiler.MemoryJavaClassFile;
import rsl.compiler.Compiler;
import rsl.core.classloaders.JarLoader;
import rsl.core.classloaders.MemoryClassLoader;
import rsl.core.coremodel.RslEntity;
import rsl.persistence.PersistenceProviders;
import rsl.persistence.RslPersistence;
import rsl.persistence.PersistenceFactory;
import rsl.util.ClassPath;
import rsl.util.Log;
import rsl.util.Random;
import rsl.util.TempDir;
import rsl.util.pubsub.PubSubBus;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RSL {

    public static PubSubBus pubSubBus = new PubSubBus("RSLCore", true, new RSLEventLogger());
    private static RslPersistence db = PersistenceFactory.getObjectDBPersistance(Config.dbDir + Config.dbName);

    public static void main(String[] args)
    {
        if(args.length != 0)
        {
            switch (args[0])
            {
                case "--compileSchemas":
                    ClassGenerationResult result = generateSchemaJARs();
                    Log.info("Generated JARs for " + result.getModelCount() + " models (" + result.getClassCount() + " classes) in: '" + new File(Config.schemaJARPath).getAbsolutePath() + "'");
                    break;

                default:
                    Log.error("Unknown argument");
            }
        }
    }

    public static void setDBPath(String dbPath){
        db = PersistenceFactory.getObjectDBPersistance(dbPath);
    }

    public static RslPersistence getDB()
    {
        return db;
    }

    public static ClassGenerationResult generateSchemaJARs()
    {
        return generateSchemaJARs(Config.schemaDir);
    }

    public static ClassGenerationResult generateSchemaJARs(String schemaDir)
    {



        int modelCount = 0;
        int classCount = 0;
        boolean containsSharedModel = Files.exists(Paths.get(schemaDir + Config.sharedSchemaFileName));
        if(!containsSharedModel){
            Log.warning("Could not find shared model '" + Config.sharedSchemaFileName + "'");
        }else{
            // start by compiling the shared model
            modelCount++;
            String sharedSchema = readSchema(new File(schemaDir + Config.sharedSchemaFileName));
            CompiledModel compiledSharedModel = Compiler.createModelByteCode(sharedSchema);
            if(compiledSharedModel.getClasses().length > 0)
            {
                Compiler.createJar(compiledSharedModel.getClasses(), Config.schemaJARPath + Config.sharedSchemaJARName);
                Log.info("Compiled shared model: " + Config.schemaJARPath + Config.sharedSchemaJARName);
                classCount += compiledSharedModel.getClasses().length;
            }
        }

        String[] schemaFiles = scanSchemaDirectory(schemaDir + "/models/");
        for (String model : schemaFiles) {
            CompiledModel compiledModel = Compiler.createModelByteCode(model);
            if(compiledModel.getClasses().length > 0)
            {
                modelCount++;
                String modelName = compiledModel.getModelDescriptor().getModelName();
                String jarName = modelName + ".jar";
                Compiler.createJar(compiledModel.getClasses(), Config.schemaJARPath + jarName);
                Log.info("Compiled model '" + modelName + "': " + Config.schemaJARPath + jarName);
                classCount += compiledModel.getClasses().length;
            }
        }

        return new ClassGenerationResult(modelCount, classCount);
    }

    public static ClassGenerationResult loadSchemaClasses()
    {
        return loadSchemaClasses(Config.schemaDir);
    }

    public static ClassGenerationResult loadSchemaClasses(String schemaDir)
    {

        ArrayList<MemoryJavaClassFile> classes = new ArrayList<>();
        int modelCount = 0;

        boolean containsSharedModel = Files.exists(Paths.get(schemaDir + Config.sharedSchemaFileName));
        if(!containsSharedModel){
            Log.warning("Could not find shared model '" + Config.sharedSchemaFileName + "'");
        }else{
            // start by compiling the shared model
            modelCount++;
            String sharedSchema = readSchema(new File(schemaDir + Config.sharedSchemaFileName));
            CompiledModel compiledSharedModel = Compiler.createModelByteCode(sharedSchema);
            classes.addAll(Arrays.asList(compiledSharedModel.getClasses()));
        }

        String[] schemaFiles = scanSchemaDirectory(schemaDir + "/models/");
        for (String model : schemaFiles) {
            modelCount++;
            CompiledModel compiledModel = Compiler.createModelByteCode(model);
            classes.addAll(Arrays.asList(compiledModel.getClasses()));
        }

        /*

        MemoryClassLoader classLoader = new MemoryClassLoader(classes, Thread.currentThread().getContextClassLoader());
        try {
            String[] classNames = classLoader.getFullClassNames();
            classLoader.loadAll();

            ClassPath.logLoadedClasses("ImageSelector", classLoader);
            Class<?> c = Class.forName("rsl.models.shared.selectors.ImageSelector", true, classLoader);
            System.out.println(c);
            c = classLoader.loadClass("rsl.models.shared.selectors.ImageSelector");
            System.out.println(c);
            ClassPath.logLoadedClasses("ImageSelector", c.getClassLoader());
            db.enhanceClasses(classNames);
        } catch (Exception e) {
            Log.error("Error when loading models into memory", e);
            return null;
        }
        */

        String jarPath = TempDir.getTempDir() + Random.randomString(12, true) + ".jar";
        JarBuilder.createJar(classes.toArray(new MemoryJavaClassFile[classes.size()]), jarPath);

        db.enhanceJARs(new String[]{jarPath});
        JarLoader.loadJar(jarPath);

        return new ClassGenerationResult(modelCount, classes.size());

    }

    // this method will not include the shared schema in the results!
    private static String[] scanSchemaDirectory(String path)
    {
        File folder = new File(path);
        FileFilter fileFilter = new WildcardFileFilter("*.json");
        File[] listOfFiles = folder.listFiles(fileFilter);
        ArrayList<String> schemas = new ArrayList<>();
        for (File file : listOfFiles != null ? listOfFiles : new File[0]) {
            if (file.isFile() && !file.getName().equals(Config.sharedSchemaFileName)) { // ignore shared model schema
                String content = readSchema(file);
                if(content != null && !content.equals("")){
                    schemas.add(content);
                }
            }
        }
        return schemas.toArray(new String[schemas.size()]);
    }

    private static String readSchema(File file)
    {
        try {
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
