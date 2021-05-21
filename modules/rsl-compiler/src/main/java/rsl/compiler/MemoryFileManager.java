package rsl.compiler;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author rroels
 *
 * The MemoryFileManager is a FileManager adapted to work with MemoryJavaClassFile. This is needed during the compilation
 * process where the Java compiler put its output into the provided MemoryFileManager one by one. As the compiler only
 * produces compiled bytecode as output (metadata is lost) we reattach previously saved metadata (see Compiler class) to
 * the compiled classes in the getJavaFileForOutput( ) method for each compiled class that comes in. This metadata is
 * needed for instance when creating JAR files. See also the Compiler class.
 */
@SuppressWarnings("unchecked")
public class MemoryFileManager extends ForwardingJavaFileManager {

    // files that are produced by the compiler are added to this list
    private final List<MemoryJavaClassFile> outputFiles;

    MemoryFileManager(JavaFileManager fileManager) {
        super(fileManager);
        this.outputFiles = new ArrayList<>();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        MemoryJavaClassFile file = new MemoryJavaClassFile(className, kind);
        outputFiles.add(file);
        return file;
    }

    List<MemoryJavaClassFile> getGeneratedOutputFiles() {
        return outputFiles;
    }
}
