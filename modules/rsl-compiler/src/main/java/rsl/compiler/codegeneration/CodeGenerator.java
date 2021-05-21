package rsl.compiler.codegeneration;

import com.squareup.javapoet.*;
import rsl.compiler.DataPrimitives;
import rsl.compiler.ModelDescriptor;
import rsl.compiler.SchemaCode;
import rsl.compiler.schemaobjects.*;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rroels
 *
 * The CodeGenerator takes a SchemaObject (internal representation of a schema file obtained after parsing the schema)
 * and generates the corresponding Java code using the JavaPoet library: https://github.com/square/javapoet
 */
public class CodeGenerator {


    /**
     * Takes a SchemaObject (internal representation of an RSL schema) and checks the schema type.
     * Depending on if the SchemaObject represents a resource, selector or model a matching class is invoked to
     * turn the object into valid Java code.
     */
    public static <T extends SchemaObject> SchemaCode[] createClassCode(T schemaObject, ModelDescriptor modelDescriptor)
    {
        Class schemaObjectType = schemaObject.getClass();

        assert(schemaObjectType == ResourceSchema.class ||
                schemaObjectType == SelectorSchema.class ||
                schemaObjectType == LinkSchema.class ||
                schemaObjectType == ModelSchema.class);

        if(schemaObjectType == ResourceSchema.class) {
            return new SchemaCode[]{ResourceGenerator.createResourceClassCode((ResourceSchema) schemaObject, modelDescriptor)};
        }else if(schemaObjectType == SelectorSchema.class) {
            return new SchemaCode[]{SelectorGenerator.createSelectorClassCode((SelectorSchema) schemaObject, modelDescriptor)};
        }else if(schemaObjectType == LinkSchema.class) {
            return new SchemaCode[]{LinkGenerator.createLinkClassCode((LinkSchema) schemaObject, modelDescriptor)};
        }else if(schemaObjectType == ModelSchema.class) {
            return ModelGenerator.createModelClassCode((ModelSchema) schemaObject);
        }else{ // should never happen
            return null;
        }

    }

    /**
     * All RSL entities have properties. This helper function generates code for an entity's properties.
     * This is done by generating private fields and corresponding getter/setter methods that can be embedded in a
     * generated class.
     *
     * @param schemaObject  The SchemaObject that represents a schema for a resource, selector or model
     * @param fieldSpecs    Generated fields will be added to this provided ArrayList
     * @param methodSpecs   Generated methods will be added to this provided ArrayList
     */
    static void embedProperties(SchemaObjectWithProperties schemaObject, ArrayList<FieldSpec> fieldSpecs, ArrayList<MethodSpec> methodSpecs)
    {
        HashMap<String,String> properties = schemaObject.getAllProperties();
        for (Map.Entry<String,String> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            String propertyType = entry.getValue();

            Class propertyTypeClass = DataPrimitives.getJavaClass(propertyType);

            // add a field for the property
            fieldSpecs.add( createField(propertyName, propertyTypeClass, Modifier.PRIVATE) );

            // add getter for the property
            methodSpecs.add( createGetter(propertyName, propertyTypeClass) );

            // add setter for the property
            methodSpecs.add( createSetter(propertyName, propertyTypeClass) );
        }
    }

    /**
     * Helper method that creates code for a class field.
     */
    static FieldSpec createField(String fieldName, Class fieldType, Modifier modifier)
    {
        return FieldSpec.builder(fieldType, fieldName)
                .addModifiers(modifier)
                .build();
    }

    /**
     * Helper method that creates code for a getter method for a specific field.
     */
    static MethodSpec createGetter(String fieldName, Class fieldType)
    {
        return MethodSpec.methodBuilder("get" + capitalizeString(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addStatement("return $L", fieldName)
                .build();
    }

    /**
     * Helper method that creates code for a setter method for a specific field.
     */
    static MethodSpec createSetter(String fieldName, Class fieldType)
    {
        ClassName RslClass = ClassName.get("rsl.core", "RSL");
        return MethodSpec.methodBuilder("set" + capitalizeString(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .returns(void.class)
                .addStatement("$L.getDB().startTransaction()", RslClass)
                .addStatement("this.$L = $L", fieldName, fieldName)
                .addStatement("$L.getDB().endTransaction()", RslClass)
                .build();
    }

    /**
     * Helper method that capitalises the first letter of a string.
     * Used in getter/setter creation methods. E.g. if a field is called "name" the getter method is called "getName".
     */
    static String capitalizeString(String str)
    {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
