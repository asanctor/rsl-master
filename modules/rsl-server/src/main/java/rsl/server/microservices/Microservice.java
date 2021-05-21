package rsl.server.microservices;



import rsl.server.ClientRequest;
import rsl.server.ServerResponse;

import java.util.HashMap;
import java.util.function.Function;

public abstract class Microservice {

    private String serviceName = "";
    private HashMap<String, Function<ClientRequest, ServerResponse>> commandMap = new HashMap<>();

    protected Microservice(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public ServerResponse executeCommand(ClientRequest req)
    {
        Function<ClientRequest, ServerResponse> fn = commandMap.getOrDefault(req.getCommand().toLowerCase(), null);
        if(fn != null)
        {
            return fn.apply(req);
        } else {
            return null;
        }

    }

    protected void registerCommand(String command, Function<ClientRequest, ServerResponse> fn)
    {
        commandMap.put(command.toLowerCase(), fn);
    }

    protected void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    String getServiceName() {
        return serviceName;
    }

    public String[] getCommandList()
    {
        return commandMap.keySet().toArray(new String[commandMap.keySet().size()]);
    }

    boolean isCommandSupported(String command)
    {
        return commandMap.containsKey(command.toLowerCase());
    }

}
