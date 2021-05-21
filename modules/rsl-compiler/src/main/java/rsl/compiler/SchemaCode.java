package rsl.compiler;

/**
 * SchemaCode is a class that contains the Java code (uncompiled) generated from an RSL schema together with some
 * metadata. The metadata can usually only be determined while parsing the schema but that data is lost once the schema
 * is translated into code. For convenience, this metadata is bundled with the code in this SchemaCode object so that
 * it can be used later. Metadata includes the name of the class, the package name, or information related to the model
 * where the class was specified.
 */
public class SchemaCode {

    private String code;
    private String className;
    private String packageName;
    private ModelDescriptor modelDescriptor;

    public SchemaCode(String className, String packageName, ModelDescriptor modelDescriptor, String code)
    {
        this.className = className;
        this.packageName = packageName;
        this.modelDescriptor = modelDescriptor;
        this.code = code;
    }


    public String getCode() {
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
