package rsl.compiler.jsonschema;

import com.google.gson.*;
import rsl.compiler.schemaobjects.LinkSchema;
import rsl.compiler.schemaobjects.ModelSchema;
import rsl.compiler.schemaobjects.ResourceSchema;
import rsl.compiler.schemaobjects.SelectorSchema;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author rroels
 *
 * A JSON deserializer for objects of the type ModelSchema. It is loaded into gson by the JSONSchemaParser class so that
 * it can be used automatically when an object of type ModelSchema should be parsed. Essentially this class specifies
 * how a model schema in JSON should be loaded into a ModelSchema object using the gson JSON library.
 */
public class JSONModelSchemaDeserializer extends JSONSchemaDeserializer implements JsonDeserializer<ModelSchema> {

    /**
     * To implement a JsonDeserializer for ModelSchema objects we just override the following method. The json parameter
     * is the root JSON element that needs to be pulled into pieces using gson methods and loaded into a ModelSchema object
     * The context parameter allows us to tell the parser to treat smaller parts of JSON as a specific type. In this
     * case it allows us to go over the array of links in the model, and to delegate the interpretation of each link
     * to the JSONLinkDeserializer class.
     */
    @Override
    public ModelSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        ModelSchema result = new ModelSchema();
        JsonObject root = json.getAsJsonObject();

        String name = root.get("name").getAsString();
        result.setName(name);

        String version = root.get("version").getAsString();
        result.setVersion(version);

        JsonArray resources = root.get("resources").getAsJsonArray();
        ResourceSchema[] resourceSchemas = new ResourceSchema[resources.size()];
        for(int i = 0; i < resources.size(); i++)
        {
            JsonElement resource = resources.get(i);
            resourceSchemas[i] = context.deserialize(resource, ResourceSchema.class);
        }
        result.setResources(resourceSchemas);

        JsonArray selectors = root.get("selectors").getAsJsonArray();
        SelectorSchema[] selectorSchemas = new SelectorSchema[selectors.size()];
        for(int i = 0; i < selectors.size(); i++)
        {
            JsonElement selector = selectors.get(i);
            selectorSchemas[i] = context.deserialize(selector, SelectorSchema.class);
        }
        result.setSelectors(selectorSchemas);

        JsonArray links = root.get("links").getAsJsonArray();
        LinkSchema[] linkSchemas = new LinkSchema[links.size()];
        for(int i = 0; i < links.size(); i++)
        {
            JsonElement link = links.get(i);
            linkSchemas[i] = context.deserialize(link, LinkSchema.class);
        }
        result.setLinks(linkSchemas);

        return result;
    }


}
