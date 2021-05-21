package rsl.server.microservices;

import rsl.server.microservices.defaultservice.DefaultService;
import rsl.server.microservices.eSPACE.eSPACEService;
import rsl.server.microservices.mindxpres.MindxpresService;

import java.util.HashMap;

public class MicroserviceManager {

    private static HashMap<String, Microservice> services = new HashMap<>();

    static {
        addService(new DefaultService());
        addService(new MindxpresService());
        addService(new eSPACEService());
    }

    public static boolean isValidCommand(String serviceName, String command)
    {
        if(serviceExists(serviceName))
        {
            Microservice service = services.get(serviceName.toLowerCase());
            return service.isCommandSupported(command.toLowerCase());
        }else{
            return false;
        }
    }

    public static String[] getRegisteredServices()
    {
        return services.keySet().toArray(new String[services.size()]);
    }

    private static boolean serviceExists(String serviceName)
    {
        return services.containsKey(serviceName.toLowerCase());
    }

    public static Microservice getMicroservice(String serviceName)
    {
        return services.getOrDefault(serviceName.toLowerCase(), null);
    }

    private static void addService(Microservice service)
    {
        services.put(service.getServiceName().toLowerCase(), service);
    }



}
