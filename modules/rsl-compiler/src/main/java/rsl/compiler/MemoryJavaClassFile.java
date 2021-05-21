package rsl.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * @author rroels
 *
 * MemoryJavaClassFile is a class that acts as a file but stores content that is written to it in memory.
 * This is achieved by extending Java's SimpleJavaFileObject and overriding some methods. Content is stored in-memory as an outputStream.
 * The purpose of this class is to capture the output (Java class bytecode) of the compiler which can only write its output to files.
 * This way we avoid writing to the filesystem just to read the result back in memory again to use the results.
 *
 * The class contains some extra fields as metadata:
 * - className: the name of the class defined in the compiled code, this include the package path, e.g. rsl.models.mindxpres.NavLink
 * - modelDescriptor: contains metadata about the RSL model that defines this class (e.g. model version and model name)
 *
 */
public class MemoryJavaClassFile extends SimpleJavaFileObject {

    private final ByteArrayOutputStream outputStream;
    private final String className;

    protected MemoryJavaClassFile(String className, Kind kind) {
        super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
        this.className = className;
        this.outputStream = new ByteArrayOutputStream();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(getBytes());
    }

    public byte[] getBytes() {
        return outputStream.toByteArray();
    }

    public String getClassName() {
        return className;
    }

}
