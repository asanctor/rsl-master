package rsl.util.contentgenerator.rsl_classes;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class RslSelector extends RslEntity  {

    public RslSelector()
    {

    }

    public RslSelector(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();


        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }
}
