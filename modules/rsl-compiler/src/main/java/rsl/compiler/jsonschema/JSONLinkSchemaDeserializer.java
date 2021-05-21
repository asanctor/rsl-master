package rsl.compiler.jsonschema;

import com.google.gson.*;
import rsl.compiler.schemaobjects.LinkSchema;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author rroels
 *
 * A JSON deserializer for objects of the type LinkSchema. It is loaded into gson by the JSONSchemaParser class so that
 * it can be used automatically when an object of type LinkSchema should be parsed. Essentially this class specifies
 * how a link schema in JSON should be loaded into a LinkSchema object using the gson JSON library.
 */
public class JSONLinkSchemaDeserializer extends JSONSchemaDeserializer implements JsonDeserializer<LinkSchema> {

    /**
     * To implement a JsonDeserializer for LinkSchema objects we just override the following method. The json parameter
     * is the root JSON element that needs to be pulled into pieces using gson methods and loaded into a LinkSchema object
     */
    @Override
    public LinkSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)  {

        LinkSchema result = new LinkSchema();
        JsonObject root = json.getAsJsonObject();

        String linkName = root.get("name").getAsString();
        result.setName(linkName);
        try{
            JsonObject sourceRestrictionsObject = root.get("sourceRestrictions").getAsJsonObject();
            String sourceRestrictionType = sourceRestrictionsObject.get("type").getAsString();
            String[] sourceRestrictionList = toStringArray(sourceRestrictionsObject.get("list").getAsJsonArray());
            result.setSourceRestrictionsType(parseRestrictionType(sourceRestrictionType));
            result.setSourceRestrictionsList(sourceRestrictionList);
        }
        //TODO: see if hack has consequences
        catch(Exception e){
            String[] sourceRestrictionEmptyList = new String[] {};
            result.setSourceRestrictionsList(sourceRestrictionEmptyList);
            System.out.println("No source or target restrictions specified (might cause problems later)");
        }
        try {
            JsonObject targetRestrictionsObject = root.get("targetRestrictions").getAsJsonObject();
            String targetRestrictionType = targetRestrictionsObject.get("type").getAsString();
            String[] targetRestrictionList = toStringArray(targetRestrictionsObject.get("list").getAsJsonArray());
            result.setTargetRestrictionsType(parseRestrictionType(targetRestrictionType));
            result.setTargetRestrictionsList(targetRestrictionList);
        }
        catch(Exception e){
            String[] targetRestrictionEmptyList = new String[] {};
            result.setTargetRestrictionsList(targetRestrictionEmptyList);
            System.out.println("No source or target restrictions specified (might cause problems later)");
        }

        JsonArray properties = root.getAsJsonArray("properties");
        parseProperties(properties, result);

        return result;
    }

    private LinkSchema.LinkRestrictionType parseRestrictionType(String type)
    {
        switch (type)
        {
            case "whitelist":
                return LinkSchema.LinkRestrictionType.WHITELIST;

            case "blacklist":
                return LinkSchema.LinkRestrictionType.BLACKLIST;

            default:
                return null;
        }
    }

    /**
     *
     * @param jarray a gson array that represents an array of unknown types
     * @return An array of strings (String[])
     */
    private String[] toStringArray(JsonArray jarray) {
        if(jarray==null){return null;}
        ArrayList<String> strings = new ArrayList<>();
        for (JsonElement el : jarray) {
            strings.add(el.getAsString());
        }
        return strings.toArray(new String[strings.size()]);
    }

}
