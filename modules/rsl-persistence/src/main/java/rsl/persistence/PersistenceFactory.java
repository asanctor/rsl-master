package rsl.persistence;

import com.ea.agentloader.AgentLoader;

public class PersistenceFactory {

    public static RslPersistence getObjectDBPersistance(String dbPath)
    {
        assert(dbPath.toLowerCase().endsWith(".odb") || dbPath.toLowerCase().endsWith(".objectdb"));
        return new ObjectDBRslPersistence(dbPath);
    }

}
