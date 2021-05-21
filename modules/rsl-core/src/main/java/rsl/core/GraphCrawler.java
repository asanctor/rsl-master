package rsl.core;

import rsl.core.coremodel.*;
import rsl.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphCrawler {

    private Set<Class<Preference>> allowedPreferences = new HashSet<>();
    private Set<Class<RslUser>> allowedUsers = new HashSet<>();
    private Set<Class<? extends RslResource>> allowedResources = new HashSet<>();
    private Set<Class<? extends RslSelector>> allowedSelectors = new HashSet<>();
    private Set<Class<? extends RslLink>> allowedLinks = new HashSet<>();

    private Set<RslEntity> results = new HashSet<>();

    public void setAllowedPreferences(Set<Class<Preference>> allowedPreferences) {
        this.allowedPreferences = allowedPreferences;
    }

    public void setAllowedUsers(Set<Class<RslUser>> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public void setAllowedResources(Set<Class<? extends RslResource>> allowedResources) {
        this.allowedResources = allowedResources;
    }

    public void setAllowedSelectors(Set<Class<? extends RslSelector>> allowedSelectors) {
        this.allowedSelectors = allowedSelectors;
    }

    public void setAllowedLinks(Set<Class<? extends RslLink>> allowedLinks) {
        this.allowedLinks = allowedLinks;
    }

    public Set<RslEntity> crawl(RslEntity root)
    {
        results.clear();
        if(canVisitEntity(root)) {
            crawlLoop(root);
        } else {
            Log.info("The provided root is not in the crawler whitelist and will not be processed");
        }
        return results;
    }

    private boolean canFollowLink(RslLink link)
    {
        return allowedLinks.contains(link.getClass());
    }

    private boolean canVisitEntity(RslEntity e)
    {
        return allowedPreferences.contains(e.getClass()) || allowedUsers.contains(e.getClass()) || allowedResources.contains(e.getClass()) || allowedSelectors.contains(e.getClass()) || allowedLinks.contains(e.getClass());
    }

    private void crawlLoop(RslEntity entity)
    {
        results.add(entity);
        if(RslResource.class.isInstance(entity)) {
            handleResource((RslResource) entity);
        }else if(RslSelector.class.isInstance(entity)) {
            handleSelector((RslSelector) entity);
        }else if(RslLink.class.isInstance(entity)) {
            handleLink((RslLink) entity);
        }
    }


    // if it's a resource, look for outgoing links and follow them
    private void handleResource(RslResource entity)
    {
        Set<RslLink> outs = entity.getOutgoingLinks().stream().filter(this::canFollowLink).collect(Collectors.toSet());
        for(RslLink l: outs)
        {
            if(!results.contains(l)){
                crawlLoop(l);
            }
        }
    }

    // if it's a selector, visit the resource that it references
    private void handleSelector(RslSelector selector)
    {
        RslResource res = selector.getSelectorResource();
        if(res != null && canVisitEntity(res) && !results.contains(res)){
            crawlLoop(res);
        }
    }

    // if it's a link, get all targets and visit them
    private void handleLink(RslLink link)
    {
        Set<RslEntity> targets = link.getTargets().stream().filter(this::canVisitEntity).collect(Collectors.toSet());
        for(RslEntity e: targets)
        {
            if(!results.contains(e)){
                crawlLoop(e);
            }
        }
    }





}
