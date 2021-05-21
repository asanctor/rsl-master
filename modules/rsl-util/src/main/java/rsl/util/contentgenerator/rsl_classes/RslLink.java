package rsl.util.contentgenerator.rsl_classes;

import org.w3c.dom.Node;
import rsl.util.contentgenerator.XMLHelper;

import java.util.*;

public abstract class RslLink extends RslEntity {

    private Set<Long> sources = new HashSet<>();
    private Set<Long> targets = new HashSet<>();

    public RslLink()
    {

    }

    public RslLink(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
    }

    public void addSources(Collection<Long> sources)
    {
        this.sources.addAll(sources);
    }

    public void addSource(long source)
    {
        this.sources.add(source);
    }

    public void addTargets(Collection<Long> targets)
    {
        this.targets.addAll(targets);
    }

    public void addTarget(long target)
    {
        this.targets.add(target);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        Node sourcesRoot = XMLHelper.createElement("sources");
        for(Long id: sources)
        {
            sourcesRoot.appendChild(XMLHelper.createElementWithText("source", id));
        }
        fieldNodes.add(sourcesRoot);

        Node targetsRoot = XMLHelper.createElement("targets");
        for(Long id: targets)
        {
            targetsRoot.appendChild(XMLHelper.createElementWithText("target", id));
        }
        fieldNodes.add(targetsRoot);

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }
}
