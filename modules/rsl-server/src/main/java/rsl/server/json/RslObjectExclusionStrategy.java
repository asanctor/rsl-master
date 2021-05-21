package rsl.server.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import rsl.core.coremodel.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RslObjectExclusionStrategy implements ExclusionStrategy {

    private static final List<String> ignoredEntityFields = Arrays.asList("incomingLinks", "outgoingLinks","editors", "sharedByTo", "accessibleByUsers", "inaccessibleByUsers");
    private static final List<String> ignoredResourceFields = Collections.singletonList("selectors");
    private static final List<String> ignoredSelectorFields = Collections.singletonList("selectorResource");
    private static final List<String> ignoredLinkFields = Arrays.asList("sources", "targets");
    private static final List<String> ignoredPreferenceFields = Arrays.asList("users");
    private static final List<String> ignoredUserFields = Arrays.asList("createdEntities", "editedEntities", "accessibleEntities", "inaccessibleEntities", "preferences", "groups", "sharedEntities", "receivedEntities");

    @Override
    public boolean shouldSkipField(FieldAttributes f) {

        Class<?> c = f.getDeclaringClass();
        if(c == RslEntity.class){
            return ignoredEntityFields.contains(f.getName());
        }else if(c == RslUser.class){
            return ignoredUserFields.contains(f.getName());
        }else if(c == RslResource.class){
            return ignoredResourceFields.contains(f.getName());
        }else if(c == RslSelector.class){
            return ignoredSelectorFields.contains(f.getName());
        }else if(c == RslLink.class){
            return ignoredLinkFields.contains(f.getName());
        }else if(c == Preference.class){
            return ignoredPreferenceFields.contains(f.getName());
        }else{
            return false;
        }
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
