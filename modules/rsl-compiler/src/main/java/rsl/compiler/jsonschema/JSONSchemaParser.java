package rsl.compiler.jsonschema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rsl.compiler.schemaobjects.*;

/**
 * @author rroels
 *
 * The JSONSchemaParser takes an RSL schema of any type (resource, selector or model) and parses it into an internal
 * representation (ResourceSchema, SelectorSchema or ModelSchema respectively).
 */
public class JSONSchemaParser {

    /**
     * @param schemaJSON A string that contains the schema in JSON
     * @param schemaObjectType  You need to specify the type of the schema so that the method knows how to interpret it
     *                          This is done by providing the class of the object that will be used to hold
     *                          the parsed schema. This can be either a ResourceSchema, SelectorSchema or ModelSchema.
     *                          Depending on which class is provided, different parsers are used to parse the schema
     *                          into an object of the desired class.
     * @return A ResourceSchema, SelectorSchema or ModelSchema containing the parsed schema
     */
    public static <T extends SchemaObject> T createSchemaObject(String schemaJSON, Class<T> schemaObjectType)
    {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // tell gson about our custom parsers for resource, selector, model and link schemas
        // depending on the value of schemaObjectType gson will automatically use the matching parser
        gsonBuilder.registerTypeAdapter(ResourceSchema.class, new JSONResourceSchemaDeserializer());
        gsonBuilder.registerTypeAdapter(SelectorSchema.class, new JSONSelectorSchemaDeserializer());
        gsonBuilder.registerTypeAdapter(LinkSchema.class, new JSONLinkSchemaDeserializer());
        gsonBuilder.registerTypeAdapter(ModelSchema.class, new JSONModelSchemaDeserializer());

        Gson gson = gsonBuilder.create();
        return gson.fromJson(schemaJSON, schemaObjectType);
    }

}
