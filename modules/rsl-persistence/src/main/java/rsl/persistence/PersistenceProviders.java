package rsl.persistence;

import rsl.util.Log;

import javax.persistence.spi.PersistenceProvider;
import java.net.URISyntaxException;
import java.util.List;
import javax.persistence.spi.PersistenceProviderResolverHolder;

public class PersistenceProviders {

    public static void getProviders()
    {
        List<PersistenceProvider> providers = getPersistenceProviders();
        for (PersistenceProvider provider : providers)
        {
            System.out.println(provider.getClass().getTypeName());
        }
    }

    public static String getProviderJARPath()
    {
        List<PersistenceProvider> providers = getPersistenceProviders();
        if(providers.size() > 0)
        {
            try {
                //Log.debug( "Provider: " + providers.get(0).getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                return providers.get(0).getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            } catch (URISyntaxException e) {
                return "";
            }
        }
        return "";
    }

    private static List<PersistenceProvider> getPersistenceProviders() {
        return PersistenceProviderResolverHolder
                .getPersistenceProviderResolver()
                .getPersistenceProviders();
    }

}
