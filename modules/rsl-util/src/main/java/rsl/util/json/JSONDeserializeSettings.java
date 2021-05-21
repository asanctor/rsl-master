package rsl.util.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;

import java.util.HashMap;
import java.util.List;

public class JSONDeserializeSettings {

    private HashMap<Class, JsonDeserializer> deserializers;

    public JSONDeserializeSettings(HashMap<Class, JsonDeserializer> deserializers)
    {
        this.deserializers = deserializers;
    }

    public HashMap<Class, JsonDeserializer> getDeserializers() {
        return deserializers;
    }

}
