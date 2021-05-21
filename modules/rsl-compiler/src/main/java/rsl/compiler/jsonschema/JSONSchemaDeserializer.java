package rsl.compiler.jsonschema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import rsl.compiler.schemaobjects.SchemaObjectWithProperties;

/**
 * @author rroels
 *
 * JSONSchemaDeserializer is a superclass for the resource/link/selector deserializers that implements some common
 * functionality (e.g. parsing the property list present in all RSL entities).
 */
class JSONSchemaDeserializer {


    /**
     * Parse the JSON array of properties and load them into the provided SchemaObject instance.
     *
     * @param properties The gson element that represents the JSON array of properties from the schema
     * @param result The parsed key/value property pairs will be written to this provided object
     */
    void parseProperties(JsonArray properties, SchemaObjectWithProperties result)
    {
        for(int i = 0; i < properties.size(); i++)
        {
            JsonObject propertyObject = properties.get(i).getAsJsonObject();
            String propertyName = propertyObject.get("name").getAsString();
            String propertyType = propertyObject.get("type").getAsString();
            result.setProperty(propertyName, propertyType);
        }
    }

}
