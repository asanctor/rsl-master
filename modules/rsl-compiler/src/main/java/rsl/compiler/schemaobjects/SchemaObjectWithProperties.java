package rsl.compiler.schemaobjects;


import java.util.HashMap;

/**
 * @author rroels
 *
 * SchemaObjectWithProperties is a base class for other schema types that implements common functionality
 * related to storing and retrieving entity properties. It is used as a super class by all types that have
 * properties (ResourceSchema, SelectorSchema and LinkSchema). The SchemaObjectWithProperties object inherits from
 *  SchemaObject which provides common fields and related methods (e.g. name field).
 */
public class SchemaObjectWithProperties extends SchemaObject {

    private HashMap<String, String> properties = new HashMap<>();

    public void setProperty(String key, String value)
    {
        properties.put(key, value);
    }

    public String getProperty(String key)
    {
        return properties.getOrDefault(key, "");
    }

    public HashMap<String,String> getAllProperties()
    {
        return properties;
    }
}
