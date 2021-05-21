package rsl.util.contentgenerator.rsl_classes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.IDGenerator;
import rsl.util.contentgenerator.XMLHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class RslEntity {

    private long id = IDGenerator.getID();

    public String name = "";
    public long creator = 0;
    public long creationTimestamp = 0;
    public Set<Long> editors = new HashSet<>();


    // https://stackoverflow.com/questions/8865099/xml-file-generator-in-java

    public RslEntity()
    {

    }

    public RslEntity(long creatorID, long creationTimestamp)
    {
        this.creator = creatorID;
        this.creationTimestamp = creationTimestamp;
    }

    public long getID()
    {
        return id;
    }

    public Node toXML()
    {
        Node entityRootNode = XMLHelper.createElement(this.getClass().getSimpleName());
        ((Element)entityRootNode).setAttribute("id", String.valueOf(id));

        Node[] entitySubNodes = this.getXMLNodes();
        for (Node node: entitySubNodes) {
            entityRootNode.appendChild(node);
        }

        return entityRootNode;
    }

    public Node[] getXMLNodes()
    {

        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.add(XMLHelper.createElementWithText("name", name));
        fieldNodes.add(XMLHelper.createElementWithText("creator", creator));
        fieldNodes.add(XMLHelper.createElementWithText("creationTimestamp", creationTimestamp));
        Node editorsRoot = XMLHelper.createElement("editors");
        for(Long id: editors)
        {
            editorsRoot.appendChild(XMLHelper.createElementWithText("editor", id));
        }
        fieldNodes.add(editorsRoot);



        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }


}
