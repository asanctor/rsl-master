package rsl.compiler.codegeneration;

import com.squareup.javapoet.*;
import com.sun.tools.javac.jvm.Code;
import rsl.compiler.ModelDescriptor;
import rsl.compiler.SchemaCode;
import rsl.compiler.schemaobjects.LinkSchema;
import rsl.compiler.schemaobjects.SchemaObject;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static rsl.compiler.codegeneration.CodeGenerator.*;

/**
 * @author rroels
 *
 * LinkGenerator is responsible for turning a LinkSchema (internal representation of a schema for an RSL link) into
 * valid Java code. This class is invoked by the CodeGenerator class when the type of the provided ObjectSchema
 * is a LinkSchema. The CodePoet library is used to create code, together with some helper methods defined in the
 * CodeGenerator class.
 *
 * Returns a SchemaCode object, which contains valid Java code (a class) that represents an RSL link defined in a schema.
 * The SchemaCode object contains additional metadata such as the package name and info about the model that defines this
 * link (e.g. model name and model version).
 */
public class LinkGenerator {

    /**
     * @param schemaObject The LinkSchema object containing the parsed link schema
     * @return The generated Java code (a class) wrapped with some metadata, see SchemaCode class
     */
    static SchemaCode createLinkClassCode(LinkSchema schemaObject, ModelDescriptor modelDescriptor) {

        final String linkPackageName = "rsl.models." + modelDescriptor.getModelName().toLowerCase() + ".links";

        // maintain a list of generated fields and methods
        // at the end of this method we add them all at once to the generated class
        ArrayList<FieldSpec> fieldSpecs = new ArrayList<>();
        ArrayList<MethodSpec> methodSpecs = new ArrayList<>();

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

        // Get typename for RslEntity so we can generate methods that accept a list of RslEntity's
        ClassName rslEntityClassName = ClassName.get("rsl.core.coremodel", "RslEntity");
        ParameterizedTypeName rslEntitySetTypeName = ParameterizedTypeName.get(ClassName.get(List.class), rslEntityClassName);



        ParameterSpec[] sourceParams = {
                ParameterSpec.builder(rslEntityClassName, "source").build(),
                ParameterSpec.builder(rslEntitySetTypeName, "sources").build()
        };

        ParameterSpec[] targetParams = {
                ParameterSpec.builder(rslEntityClassName, "target").build(),
                ParameterSpec.builder(rslEntitySetTypeName, "targets").build()
        };


        // constructors
        for(int i = 0; i < sourceParams.length; i++)
        {
            for(int j = 0; j < targetParams.length; j++)
            {
                methodSpecs.add(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(sourceParams[i])
                        .addParameter(targetParams[j])
                        .addStatement("super(" + sourceParams[i].name + ", " + targetParams[j].name + ")")
                        .build());
            }
        }


        ClassName linkClassName = ClassName.get(linkPackageName, schemaObject.getName());

        // generate static factory method that returns a new instance of its own type
        for(int i = 0; i < sourceParams.length; i++)
        {
            for(int j = 0; j < targetParams.length; j++)
            {
                methodSpecs.add( MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.STATIC)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(sourceParams[i])
                        .addParameter(targetParams[j])
                        .returns(ClassName.get(linkPackageName, schemaObject.getName()))
                        .addStatement("return new $L(" + sourceParams[i].name + ", " + targetParams[j].name + ")", linkClassName)
                        .build());
            }
        }

        // add fields, getters and setters for an RSL link's properties
        CodeGenerator.embedProperties(schemaObject, fieldSpecs, methodSpecs);

        // get ClassName object for the superclass (RslResource)
        ClassName superType = ClassName.get("rsl.core.coremodel", "RslLink");


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
        TypeSpec linkClass = classBuilder.build();

        // create the code above the class (e.g. package name and import statements) and add class code underneath
        JavaFile javaFile = JavaFile.builder(linkPackageName, linkClass)
                .build();

        // wrap generated code in a SchemaCode object with all the relevant metadata and return it
        return new SchemaCode(schemaObject.getName(), linkPackageName, modelDescriptor, javaFile.toString());
    }

}
