package rsl.server.interfaces;


import rsl.server.ClientRequest;
import rsl.server.RequestHandler;
import rsl.server.ServerResponse;
import rsl.util.Log;

import java.util.Properties;

public abstract class ClientCommunicationInterface {

    private Properties config = null;
    private String interfaceName = null;
    protected Properties defaultProperties = new Properties();

    protected ClientCommunicationInterface(String interfaceName, Properties config)
    {
        Properties configWithDefaults = new Properties(getDefaultProperties());
        if(config != null){
            configWithDefaults.putAll(config);
        }
        this.interfaceName = interfaceName;
        this.config = configWithDefaults;
    }

    public abstract void start();

    public abstract void stop();

    public abstract void sendReply(Object clientInterfaceState, ServerResponse response);

    protected Properties getProperties()
    {
        return config;
    }

    public abstract Properties getDefaultProperties();

    public String getInterfaceName()
    {
        return this.interfaceName;
    }

}
