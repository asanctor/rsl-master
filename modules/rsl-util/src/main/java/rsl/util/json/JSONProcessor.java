package rsl.util.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import rsl.util.Log;

import java.util.*;

public class JSONProcessor {

    public static String toJSON(Object o)
    {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static String toJSON(Object o, JSONSerializeSettings settings)
    {
        GsonBuilder builder = new GsonBuilder();

        HashMap<Class, JsonSerializer> serializers = settings.getSerializers();
        if(serializers != null)
        {
            for(Map.Entry<Class, JsonSerializer> entry : serializers.entrySet())
            {
                Class c = entry.getKey();
                JsonSerializer serializer = entry.getValue();
                builder.registerTypeAdapter(c, serializer);
            }
        }

        List<ExclusionStrategy> exclusionStrategies = settings.getExclusionStrategies();
        if(exclusionStrategies != null)
        {
            for(ExclusionStrategy strat : exclusionStrategies)
            {
                builder.addSerializationExclusionStrategy(strat);
            }
        }

        List<TypeAdapterFactory> adapterFactories = settings.getAdapterFactories();
        if(adapterFactories != null)
        {
            for(TypeAdapterFactory factory : adapterFactories)
            {
                builder.registerTypeAdapterFactory(factory);
            }
        }

        builder.serializeNulls();
        Gson gson = builder.create();

        return gson.toJson(o);

    }

    public static <T> T fromJSON(String json, Class<T> type)
    {
        return fromJSON(json, new JSONDeserializeSettings(null), type);
    }

    public static <T> T fromJSON(String json, JSONDeserializeSettings settings, Class<T> type)
    {
        try {
            GsonBuilder builder = new GsonBuilder();

            HashMap<Class, JsonDeserializer> deserializers = settings.getDeserializers();
            if(deserializers != null)
            {
                for(Map.Entry<Class, JsonDeserializer> entry : deserializers.entrySet())
                {
                    Class c = entry.getKey();
                    JsonDeserializer deserializer = entry.getValue();
                    builder.registerTypeHierarchyAdapter(c, deserializer);
                }
            }

            Gson gson = builder.create();
            return gson.fromJson(json, type);
        }catch (Exception e) {
            return null;
        }
    }



    public static <T> List<T> extractArrayField(String json, JSONObjectPath objPath, Class<T> type )
    {
        JsonElement el = extractPath(json, objPath);
        if(el==null || !el.isJsonArray()) {
            return null;
        }else{
            return new Gson().fromJson(el, new TypeToken<List<T>>(){}.getType());
        }
    }

    private static JsonElement extractPath(String json, JSONObjectPath objPath)
    {
        JsonElement el = new Gson().fromJson(json, JsonElement.class);
        for(String pathEl : objPath)
        {
            if(el.isJsonObject()) {
                el = el.getAsJsonObject().get(pathEl);
            } else if (el.isJsonArray()) {
                el = el.getAsJsonArray().get(Integer.parseInt(pathEl));
            } else if (el.isJsonPrimitive()) {
                el = el.getAsJsonPrimitive();
            } else if (el.isJsonNull()) {
                el = el.getAsJsonNull();
            }
        }
        return el;
    }

    public static String extractStringField(String json, String valueName)
    {
        return extractStringField(json, new JSONObjectPath(valueName));
    }

    public static String extractStringField(String json, JSONObjectPath objPath)
    {
        JsonElement el = extractPath(json, objPath);
        if(el==null || el.isJsonNull()) {
            return null;
        }else{
            return el.getAsString();
        }
    }

}
