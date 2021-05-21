package rsl.compiler.schemaobjects;

import java.util.HashMap;

/**
 * @author rroels
 *
 * SchemaObject is a base class for other schema types that implements common functionality. It is used as a super class
 * by SchemaObjectWithProperties as well as ModelSchema.
 */
public class SchemaObject {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
