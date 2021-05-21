package rsl.compiler.jsonschema;

import com.google.gson.*;
import rsl.compiler.schemaobjects.SelectorSchema;

import java.lang.reflect.Type;

/**
 * @author rroels
 *
 * A JSON deserializer for objects of the type SelectorSchema. It is loaded into gson by the JSONSchemaParser class so that
 * it can be used automatically when an object of type SelectorSchema should be parsed. Essentially this class specifies
 * how a model schema in JSON should be loaded into a SelectorSchema object using the gson JSON library.
 */
public class JSONSelectorSchemaDeserializer extends JSONSchemaDeserializer implements JsonDeserializer<SelectorSchema> {

    /**
     * To implement a JsonDeserializer for SelectorSchema objects we just override the following method. The json parameter
     * is the root JSON element that needs to be pulled into pieces using gson methods and loaded into a SelectorSchema object
     */
    @Override
    public SelectorSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        SelectorSchema result = new SelectorSchema();
        JsonObject root = json.getAsJsonObject();

        String name = root.get("name").getAsString();
        result.setName(name);

        String refersTo = root.get("refersTo").getAsString();
        result.setRefersTo(refersTo);

        JsonArray properties = root.getAsJsonArray("properties");
        parseProperties(properties, result);

        return result;
    }

}
