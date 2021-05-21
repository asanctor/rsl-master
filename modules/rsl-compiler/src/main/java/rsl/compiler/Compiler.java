package rsl.compiler;

import com.sun.tools.internal.ws.processor.model.ModelObject;
import org.apache.commons.io.FileUtils;
import rsl.compiler.codegeneration.CodeGenerator;
import rsl.compiler.jsonschema.JSONSchemaParser;
import rsl.compiler.schemaobjects.ModelSchema;
import rsl.compiler.schemaobjects.SchemaObject;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// todo: investigate http://bytebuddy.net/
// https://gist.github.com/chrisvest/9873843
// http://www.soulmachine.me/blog/2015/07/22/compile-and-run-java-source-code-in-memory/

/**
 * @author rroels
 *
 * The Compiler class is the main entry point for the compiler submodule and provides high-level methods for parsing
 * and compiling RSL schema files.
 */
public class Compiler {

    /**
     * createModelByteCode takes an array of strings where each string contains the content of a schema file. The method
     * then parse them, compile them, and return the resulting array of Java class files.
     *
     * @return An array of compiled Java classes (bytecode). One schema could potentially result in more than one class
     * files. For instance, a model schema might contain multiple link definitions which will result in a new class for
     * each link, and thus also a new Java class file for each link.
     */

    public static CompiledModel createModelByteCode(String schema)
    {

        ModelSchema modelSchema = JSONSchemaParser.createSchemaObject(schema, ModelSchema.class);
        ModelDescriptor modelDescriptor = new ModelDescriptor(modelSchema.getName(), modelSchema.getVersion());

        ArrayList<MemoryJavaSourceFile> classSourceFiles = new ArrayList<>();
        SchemaCode[] generatedCode = CodeGenerator.createClassCode(modelSchema, modelDescriptor);
        if (generatedCode != null) {
            for (SchemaCode code : generatedCode) {
                MemoryJavaSourceFile newFile = new MemoryJavaSourceFile(
                        code.getClassName(), code.getPackageName(),
                        code.getModelDescriptor(), code.getCode());
                classSourceFiles.add(newFile);
            }
        }

        if(Config.outputClassCode)
        {
            for(MemoryJavaSourceFile sourceFile: classSourceFiles)
            {
                File outFile = new File(Config.outputClassCodeDir + sourceFile.getClassName() + ".java");
                try {
                    FileUtils.write(outFile, sourceFile.getCharContent(true), "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        MemoryJavaSourceFile[] sources = classSourceFiles.toArray(new MemoryJavaSourceFile[classSourceFiles.size()]);
        MemoryJavaClassFile[] classes = compileClassSources(sources);

        return new CompiledModel(classes, modelDescriptor);
    }

    /**
     * Create a valid JAR archive of the provided MemoryJavaClassFiles
     */
    public static void createJar(MemoryJavaClassFile[] compiledClasses, String jarPath)
    {
        JarBuilder.createJar(compiledClasses, jarPath);
    }

    /**
     * Invoke the internal Java compiler to compile the provided MemoryJavaSourceFiles. Note that the compiler normally
     * only takes files as input. We work around this by implementing a file interface on top of a string to trick the
     * compiler into reading the code from memory instead of from the filesystem. See MemoryJavaSourceFile. Similarly we
     * use MemoryJavaClassFile objects to make the compiler write its results back to memory instead of writing it to
     * the filesystem.
     */
    private static MemoryJavaClassFile[] compileClassSources(MemoryJavaSourceFile[] classSources)
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final MemoryFileManager fileManager = new MemoryFileManager(compiler.getStandardFileManager(null, null, null));

        List<String> optionList = new ArrayList<>();  // set compiler's classpath to be same as the runtime's
        optionList.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));

        final JavaCompiler.CompilationTask task = compiler.getTask( null, fileManager, diagnostics, optionList, null, Arrays.asList(classSources));

        if(task.call())
        {
            List<MemoryJavaClassFile> outputFiles = fileManager.getGeneratedOutputFiles();
            return outputFiles.toArray(new MemoryJavaClassFile[outputFiles.size()]);
        }
        else
        {
            for( final Diagnostic< ? extends JavaFileObject > diagnostic: diagnostics.getDiagnostics() ) {
                System.out.format("%s, line %d in %s",
                    diagnostic.getMessage( null ),
                    diagnostic.getLineNumber(),
                    diagnostic.getSource().getName() );
            }
            return new MemoryJavaClassFile[0];
        }

    }



}
