package rsl.compiler;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * @author rroels
 *
 * MemoryJavaSourceFile is a class that allows a string to be accessed as if it's a file.
 * This is achieved by extending Java's SimpleJavaFileObject and overriding some methods.
 * The purpose of this class is to pass generated code (string) directly to the compiler,
 * removing the need to write to the filesystem (slow).
 *
 * The class contains some extra fields as metadata:
 * - className: the name of the class defined in the code
 * - packageName: the name of the class's package
 * - modelDescriptor: contains metadata about the RSL model that defines this class (e.g. model version and model name)
 *
 */
public class MemoryJavaSourceFile extends SimpleJavaFileObject {

    private final String code;
    private final String className;
    private final String packageName;
    private final ModelDescriptor modelDescriptor;

    MemoryJavaSourceFile(String className, String packageName, ModelDescriptor modelDescriptor, String code) {
        super(URI.create("string:///" + className.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
        this.packageName = packageName;
        this.modelDescriptor = modelDescriptor;
        this.className = className;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public ModelDescriptor getModelDescriptor() {
        return modelDescriptor;
    }
}