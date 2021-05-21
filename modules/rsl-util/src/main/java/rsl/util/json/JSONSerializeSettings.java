package rsl.util.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;

import java.util.HashMap;
import java.util.List;

public class JSONSerializeSettings {

    private HashMap<Class, JsonSerializer> serializers;
    private List<ExclusionStrategy> exclusionStrategies;
    private List<TypeAdapterFactory> adapterFactories;

    public JSONSerializeSettings(HashMap<Class, JsonSerializer> serializers, List<ExclusionStrategy> exclusionStrategies, List<TypeAdapterFactory> adapterFactories)
    {
        this.serializers = serializers;
        this.exclusionStrategies = exclusionStrategies;
        this.adapterFactories = adapterFactories;
    }

    public HashMap<Class, JsonSerializer> getSerializers() {
        return serializers;
    }

    public List<ExclusionStrategy> getExclusionStrategies() {
        return exclusionStrategies;
    }

    public List<TypeAdapterFactory> getAdapterFactories() {
        return adapterFactories;
    }
}
