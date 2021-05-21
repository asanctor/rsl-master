package rsl.compiler.codegeneration;

import com.squareup.javapoet.*;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import rsl.compiler.ModelDescriptor;
import rsl.compiler.SchemaCode;
import rsl.compiler.schemaobjects.LinkSchema;
import rsl.compiler.schemaobjects.ModelSchema;
import rsl.compiler.schemaobjects.ResourceSchema;
import rsl.compiler.schemaobjects.SelectorSchema;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static rsl.compiler.codegeneration.LinkGenerator.createLinkClassCode;
import static rsl.compiler.codegeneration.ResourceGenerator.createResourceClassCode;
import static rsl.compiler.codegeneration.SelectorGenerator.createSelectorClassCode;

/**
 * @author rroels
 *
 * ModelGenerator is responsible for turning a ModelSchema (internal representation of a schema for an RSL model) into
 * valid Java code. This class is invoked by the CodeGenerator class when the type of the provided ObjectSchema
 * is a ModelSchema. The CodePoet library is used to create code, together with some helper methods defined in the
 * CodeGenerator class.
 *
 * A model is an app-specific collection of links. For each model a helper class is generated which will act as a
 * superclass for the links that are part of the model. The name of the helper class is defined as modelName + "Link".
 * Then, a new class is generated for each link defined in the model with the help of the LinkGenerator class. Each
 * of these links inherits from the previously mentioned helper class.
 *
 * Returns an array of SchemaCode objects, each containing a valid Java class that represents one of the links defined
 * in the schema. The generated helper class is also included in this array.
 * A SchemaCode object contains additional metadata such as the package name and info about the model that defines the
 * link (e.g. model name and model version).
 */
public class ModelGenerator {

    /**
     * @param schemaObject The ModelSchema object containing the parsed model schema
     * @return The generated Java classes wrapped with some metadata, see SchemaCode class
     */
    static SchemaCode[] createModelClassCode(ModelSchema schemaObject)
    {
        String modelName = schemaObject.getName();
        String modelVersion = schemaObject.getVersion();

        ModelDescriptor modelDescriptor = new ModelDescriptor(modelName, modelVersion);

        ArrayList<SchemaCode> result = new ArrayList<>();

        ResourceSchema[] resources = schemaObject.getResources();
        for(int i = 0; i < resources.length; i++)
        {
            result.add(createResourceClassCode(resources[i], modelDescriptor));
        }

        SelectorSchema[] selectors = schemaObject.getSelectors();
        for(int i = 0; i < selectors.length; i++)
        {
            result.add(createSelectorClassCode(selectors[i], modelDescriptor));
        }

        LinkSchema[] links = schemaObject.getLinks();
        for(int i = 0; i < links.length; i++)
        {
            result.add(createLinkClassCode(links[i], modelDescriptor));
        }

        return result.toArray(new SchemaCode[result.size()]);
    }


    /**
     * @param modelName The name of the model for which the helper class will be generated
     * @param modelVersion The version of the model for which the helper class will be generated
     * @return The generated Java code (a class) wrapped with some metadata, see SchemaCode class
     */
    /*
    static SchemaCode createModelLinkSuperClass(String modelName, String modelVersion)
    {

        // maintain a list of generated methods
        // at the end of this method we add them all at once to the generated class
        ArrayList<MethodSpec> methodSpecs = new ArrayList<>();

        // the name of this helper class is defined as modelName + "Link"
        // e.g. MindxpresLink for links in the Mindxpres model
        final String className = modelName + "Link";

        // the packagename of the generated helper class
        // e.g. rsl.models.mindxpres
        final String packageName = CodeGenerator.linkPackageName + modelName;

        // get ParameterizedTypeName object so we can created method that returns links of any type
        ParameterizedTypeName constructorType = ParameterizedTypeName.get(
                ClassName.get(Constructor.class),
                TypeVariableName.get("T"));

        // get ParameterizedTypeName so we can create method that takes generic argument of type Class as parameter
        ParameterizedTypeName linkType = ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("T"));

        // helper factory method that creates link of specified type between 2 specified entities
        methodSpecs.add( MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.STATIC)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("T", ClassName.get(packageName, className)))
                .addParameter(linkType, "linkType")
                .addParameter(ClassName.get("rsl.core.coremodel", "RslEntity"), "source")
                .addParameter(ClassName.get("rsl.core.coremodel", "RslEntity"), "target")
                .returns(TypeVariableName.get("T"))
                .beginControlFlow("try")
                .addStatement("$T constructor = linkType.getConstructor(RslEntity.class, RslEntity.class)", constructorType)
                .addStatement("return (T)constructor.newInstance(source, target)")
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .addStatement("return null")
                .build());


        // Get typename for RslEntity so we can generate methods that accept a list of RslEntity's
        ClassName rslEntityClassName = ClassName.get("rsl.core.coremodel", "RslEntity");
        ParameterizedTypeName rslEntitySetTypeName = ParameterizedTypeName.get(ClassName.get(List.class), rslEntityClassName);

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

        // constructor(RslEntity source, RslEntity target)
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rslEntityClassName, "source")
                .addParameter(rslEntityClassName, "target")
                .addStatement("super(source, target)")
                .build());

        // constructor(List<RslEntity> sources, RslEntity target)
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rslEntitySetTypeName, "sources")
                .addParameter(rslEntityClassName, "target")
                .addStatement("super(sources, target)")
                .build());

        // constructor(RslEntity source, List<RslEntity> targets)
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rslEntityClassName, "source")
                .addParameter(rslEntitySetTypeName, "targets")
                .addStatement("super(source, targets)")
                .build());

        // constructor(List<RslEntity> sources, List<RslEntity> targets)
        methodSpecs.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rslEntitySetTypeName, "sources")
                .addParameter(rslEntitySetTypeName, "targets")
                .addStatement("super(sources, targets)")
                .build());

        // get ClassName object for the superclass (RslLink)
        ClassName superType = ClassName.get("rsl.core.coremodel", "RslLink");

        // create the new class, make sure it is annotated with @Entity
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(ClassName.get("javax.persistence","Entity"))
                .superclass(superType);

        // add all previously built methods to the class
        for (MethodSpec methodSpec: methodSpecs) { classBuilder.addMethod(methodSpec); }

        // create the code for the class
        TypeSpec resourceClass = classBuilder.build();

        // create the code above the class (e.g. package name and import statements) and add class code underneath
        JavaFile javaFile = JavaFile.builder(packageName, resourceClass)
                .build();

        // wrap generated code in a SchemaCode object with all the relevant metadata and return it
        ModelDescriptor modelDescriptor = new ModelDescriptor(modelName, modelVersion);
        return new SchemaCode(className, packageName, modelDescriptor, javaFile.toString());
    }
    */

}
