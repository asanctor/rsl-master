package rsl.compiler.jsonschema;

import com.google.gson.*;
import rsl.compiler.schemaobjects.ResourceSchema;

import java.lang.reflect.Type;

/**
 * @author rroels
 *
 * A JSON deserializer for objects of the type ResourceSchema. It is loaded into gson by the JSONSchemaParser class so that
 * it can be used automatically when an object of type ResourceSchema should be parsed. Essentially this class specifies
 * how a model schema in JSON should be loaded into a ResourceSchema object using the gson JSON library.
 */
public class JSONResourceSchemaDeserializer extends JSONSchemaDeserializer implements JsonDeserializer<ResourceSchema> {

    /**
     * To implement a JsonDeserializer for ResourceSchema objects we just override the following method. The json parameter
     * is the root JSON element that needs to be pulled into pieces using gson methods and loaded into a ResourceSchema object
     */
    @Override
    public ResourceSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        ResourceSchema result = new ResourceSchema();
        JsonObject root = json.getAsJsonObject();

        String name = root.get("name").getAsString();
        result.setName(name);

        JsonArray properties = root.getAsJsonArray("properties");
        parseProperties(properties, result);

        return result;
    }

}
