package rsl.util.contentgenerator.presentationclasses.links;

import org.fluttercode.datafactory.impl.DataFactory;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.rsl_classes.RslLink;

import java.util.ArrayList;
import java.util.Arrays;

public class StructuralLink extends RslLink {

    public StructuralLink()
    {
        this(0,0);
    }

    public StructuralLink(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
        DataFactory df = new DataFactory();
    }


    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }

}
