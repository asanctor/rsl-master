package rsl.server.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import rsl.core.coremodel.RslEntity;

@SuppressWarnings("unchecked")
public class RslJsonTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

        if (!RslEntity.class.isAssignableFrom(type.getRawType())) return null;

        final TypeAdapter delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonObject> objectAdapter = gson.getAdapter(JsonObject.class);



        return (TypeAdapter<T>) new RslJsonTypeAdapter(delegate, objectAdapter);
    }

}