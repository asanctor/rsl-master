package rsl.server.json;

import com.google.gson.*;
import rsl.util.Log;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestListDeserializer implements JsonDeserializer<List<Object>> {

    public List<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        List<Object> m = new ArrayList<Object>();
        JsonArray arr = json.getAsJsonArray();
        for (JsonElement jsonElement : arr) {
            if (jsonElement.isJsonObject()) {
                m.add(context.deserialize(jsonElement, Map.class));
            } else if (jsonElement.isJsonArray()) {
                m.add(context.deserialize(jsonElement, List.class));
            } else if (jsonElement.isJsonPrimitive()) {
                Number num = null;
                try {
                    num = NumberFormat.getInstance().parse(jsonElement.getAsString());
                } catch (Exception e) {
                }
                if (num != null) {
                    m.add(num);
                    continue;
                }
                JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
                if (prim.isBoolean()) {
                    m.add(prim.getAsBoolean());
                } else if (prim.isString()) {
                    m.add(prim.getAsString());
                } else {
                    m.add(null);
                }
            }
        }
        return m;
    }
}