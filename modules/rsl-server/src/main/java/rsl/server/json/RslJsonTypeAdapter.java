package rsl.server.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.core.coremodel.RslResource;
import rsl.core.coremodel.RslSelector;
import rsl.util.json.JSONProcessor;
import rsl.util.json.JSONSerializeSettings;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RslJsonTypeAdapter<T> extends TypeAdapter<T> {

    private final TypeAdapter delegate;
    private final TypeAdapter<JsonObject> objectAdapter;

    RslJsonTypeAdapter(TypeAdapter delegate, TypeAdapter<JsonObject> objectAdapter)
    {
        this.delegate = delegate;
        this.objectAdapter = objectAdapter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(JsonWriter out, T value) throws IOException {

        JsonObject jobj = (JsonObject) delegate.toJsonTree(value);

        if(RslResource.class.isAssignableFrom(value.getClass()))
        {
            applyResourceConversion((RslResource) value, jobj);
            jobj.addProperty("rslType", "RslResource");
        }else if(RslSelector.class.isAssignableFrom(value.getClass())) {
            applySelectorConversion((RslSelector) value, jobj);
            jobj.addProperty("rslType", "RslSelector");
        }else if(RslLink.class.isAssignableFrom(value.getClass())) {
            applyLinkConversion((RslLink) value, jobj);
            jobj.addProperty("rslType", "RslLink");
        }

        try {
            objectAdapter.write(out, jobj);
        } catch (IOException e) {
            objectAdapter.write(out, null);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T read(JsonReader in) throws IOException {
        try {
            return (T) delegate.read(in);
        } catch (IOException | IllegalStateException | JsonSyntaxException e) {
            in.skipValue();
            return null;
        }
    }


    public static String convertEntity(RslEntity entity)
    {
        List<ExclusionStrategy> exclusionStrategies = Collections.singletonList(new RslObjectExclusionStrategy());
        List<TypeAdapterFactory> adapterFactories = Collections.singletonList(new RslJsonTypeAdapterFactory());

        return JSONProcessor.toJSON(entity, new JSONSerializeSettings(null, exclusionStrategies, adapterFactories));
    }

    private static void applyResourceConversion(RslResource resource, JsonObject jobj)
    {

        // add the incomingLinks and outgoingLinks as list of IDs
        addInOutLinksAsIDs(resource, jobj);

        // add the selectors as list of IDs
        Set<RslSelector> selectors = resource.getSelectors();
        JsonArray JsonSelectors = new JsonArray();
        for(RslSelector selector : selectors)
        {
            JsonSelectors.add(selector.getID());
        }
        jobj.add("selectors", JsonSelectors);

    }

    private static void applySelectorConversion(RslSelector selector, JsonObject jobj)
    {

        // add the incomingLinks and outgoingLinks as list of IDs
        addInOutLinksAsIDs(selector, jobj);

        // add the selectorResource as an ID
        RslResource selectorResource = selector.getSelectorResource();
        if(selectorResource == null)
        {
            jobj.add("selectorResource", JsonNull.INSTANCE);
        } else {
            jobj.addProperty("selectorResource", selectorResource.getID());
        }

    }

    private static void applyLinkConversion(RslLink link, JsonObject jobj)
    {

        // add the incomingLinks and outgoingLinks as list of IDs
        addInOutLinksAsIDs(link, jobj);

        Set<RslEntity> sources = link.getSources();
        JsonArray JsonSources = new JsonArray();
        for(RslEntity source : sources)
        {
            JsonSources.add(source.getID());
        }
        jobj.add("sources", JsonSources);

        Set<RslEntity> targets = link.getTargets();
        JsonArray JsonTargets = new JsonArray();
        for(RslEntity target : targets)
        {
            JsonTargets.add(target.getID());
        }
        jobj.add("targets", JsonTargets);

    }

    private static void addInOutLinksAsIDs(RslEntity entity, JsonObject jobj)
    {
        Set<RslLink> inLinks = entity.getIncomingLinks();
        JsonArray JsonInLinks = new JsonArray();
        for(RslLink inLink : inLinks)
        {
            JsonInLinks.add(inLink.getID());
        }

        Set<RslLink> outLinks = entity.getOutgoingLinks();
        JsonArray JsonOutLinks = new JsonArray();
        for(RslLink outLink : outLinks)
        {
            JsonOutLinks.add(outLink.getID());
        }

        jobj.add("outgoingLinks", JsonOutLinks);
        jobj.add("incomingLinks", JsonInLinks);

    }


}
