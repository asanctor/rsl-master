package rsl.server.json;

import com.google.gson.*;
import rsl.util.Log;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

public class RequestMapDeserializer implements JsonDeserializer<Map<String, Object>> {

    public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Map<String, Object> m = new LinkedHashMap<String, Object>();
        JsonObject jo = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> mx : jo.entrySet()) {
            String key = mx.getKey();
            JsonElement v = mx.getValue();
            if (v.isJsonArray()) {
                List a = context.deserialize(v, List.class);
                m.put(key, a);
            } else if (v.isJsonPrimitive()) {
                Number num = null;
                ParsePosition position=new ParsePosition(0);
                String vString=v.getAsString();
                try {
                    num = NumberFormat.getInstance(Locale.ROOT).parse(vString,position);
                } catch (Exception e) {
                }
                //Check if the position corresponds to the length of the string
                if(position.getErrorIndex() < 0 && vString.length() == position.getIndex()) {
                    if (num != null) {
                        m.put(key, num);
                        continue;
                    }
                }
                JsonPrimitive prim = v.getAsJsonPrimitive();
                if (prim.isBoolean()) {
                    m.put(key, prim.getAsBoolean());
                } else if (prim.isString()) {
                    m.put(key, prim.getAsString());
                } else {
                    m.put(key, null);
                }

            } else if (v.isJsonObject()) {
                m.put(key, context.deserialize(v, Map.class));
            }
        }
        return m;
    }
}

