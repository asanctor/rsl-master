package rsl.server;

import com.toddfast.util.convert.TypeConverter;
import rsl.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ClientRequest {

    public String raw = "";
    private String command = "";
    private String serviceName = "";
    private HashMap<String, Object> parameters = new HashMap<>();

    public ClientRequest(String command, HashMap<String, Object> queryParameters)
    {
        this.serviceName = "";
        this.command = command;
        this.parameters = queryParameters;
    }

    public ClientRequest(String serviceName, String command, HashMap<String, Object> queryParameters)
    {
        this.serviceName = serviceName;
        this.command = command;
        this.parameters = queryParameters;
    }

    public String getCommand() {
        return command == null ? "" : command;
    }

    public String getServiceName() {
        return serviceName == null ? "" : serviceName;
    }

    public HashMap<String, Object> getQueryParameters() {
        return parameters == null ? new HashMap<>() : parameters;
    }

    @SuppressWarnings("unchecked")
    public <T> T getQueryParameter(String parameterName, Class<T> parameterType)
    {

        Object o = parameters.getOrDefault(parameterName, null);

        // Gson (the JSON parsing lib) serializes a json object into a LinkedTreeMap
        // if the user expects a different Map (e.g. a HashMap), do the conversion to the requested Map type
        if((Map.class.isAssignableFrom(parameterType)) && (Map.class.isAssignableFrom(o.getClass())) && o.getClass() != parameterType)
        {
            Map originalMap = (Map) o;
            try {
                Map newMap = (Map) parameterType.newInstance();
                newMap.putAll(originalMap);
                o = newMap;
            } catch (Exception e) {
                return null;
            }
        }

        if(o == null)
        {
            return null;
        } else {
            T castedObject = null;
            if(o.getClass().isAssignableFrom(parameterType)) {
                castedObject = ((T) o);
            } else {
                try {
                    castedObject = TypeConverter.convert(parameterType, o);
                } catch(Exception e) {

                }
            }
            return castedObject;
        }
    }

    public String getRawRequest()
    {
        return raw;
    }
}