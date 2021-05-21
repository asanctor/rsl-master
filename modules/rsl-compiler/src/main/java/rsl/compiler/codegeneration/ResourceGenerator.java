package rsl.compiler.codegeneration;

import com.squareup.javapoet.*;
import rsl.compiler.ModelDescriptor;
import rsl.compiler.SchemaCode;
import rsl.compiler.schemaobjects.ResourceSchema;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;

import static rsl.compiler.codegeneration.CodeGenerator.*;

/**
 * @author rroels
 *
 * ResourceGenerator is responsible for turning a ResourceSchema (internal representation of a schema for an RSL resource)
 * into valid Java code. This class is invoked by the CodeGenerator class when the type of the provided ObjectSchema
 * is a ResourceSchema. The CodePoet library is used to create code, together with some helper methods defined in the
 * CodeGenerator class.
 *
 * Returns a SchemaCode object, which contains valid Java code (a class) that represents an RSL resource defined in a schema.
 * The SchemaCode object contains additional metadata such as the package name and info about the model that defines this
 * resource (e.g. model name and model version).
 */
public class ResourceGenerator {

    /**
     * @param schemaObject The ResourceSchema object containing the parsed resource schema
     * @return The generated Java code (a class) wrapped with some metadata, see SchemaCode class
     */
    static SchemaCode createResourceClassCode(ResourceSchema schemaObject, ModelDescriptor modelDescriptor)
    {

        final String resourcePackageName = "rsl.models." + modelDescriptor.getModelName().toLowerCase() + ".resources";

        // maintain a list of generated fields and methods
        // at the end of this method we add them all at once to the generated class
        ArrayList<FieldSpec> fieldSpecs = new ArrayList<>();
        ArrayList<MethodSpec> methodSpecs = new ArrayList<>();

        // add fields, getters and setters for all resource properties
        embedProperties(schemaObject, fieldSpecs, methodSpecs);

        // add default constructor
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build());

        // constructor(boolean save), boolean specifies if object should be stored upon creation or not
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Boolean.class, "save")
                .addStatement("super(save)")
                .build());

        // get ClassName object for the superclass (RslResource)
        ClassName superType = ClassName.get("rsl.core.coremodel", "RslResource");

        // create the new class, make sure it is annotated with @Entity
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(schemaObject.getName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("javax.persistence","Entity"))
                .superclass(superType);


        // add all previously built fields to the class
        for (FieldSpec fieldSpec: fieldSpecs) { classBuilder.addField(fieldSpec); }

        // add all previously built methods to the class
        for (MethodSpec methodSpec: methodSpecs) { classBuilder.addMethod(methodSpec); }

        // build the class
        TypeSpec resourceClass = classBuilder.build();

        // create the code above the class (e.g. package name and import statements) and add class code underneath
        JavaFile javaFile = JavaFile.builder(resourcePackageName, resourceClass)
                .build();

        // wrap generated code in a SchemaCode object with all the relevant metadata and return it
        return new SchemaCode(schemaObject.getName(), resourcePackageName, modelDescriptor, javaFile.toString());
    }

}
