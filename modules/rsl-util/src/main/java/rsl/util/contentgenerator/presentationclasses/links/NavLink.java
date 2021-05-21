package rsl.util.contentgenerator.presentationclasses.links;

import org.fluttercode.datafactory.impl.DataFactory;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.XMLHelper;
import rsl.util.contentgenerator.rsl_classes.RslLink;

import java.util.ArrayList;
import java.util.Arrays;

public class NavLink extends RslLink {

    public int weight = 0;

    public NavLink()
    {
        this(0,0);
    }

    public NavLink(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
        DataFactory df = new DataFactory();
        this.weight = df.getNumberBetween(1, 5);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.add(XMLHelper.createElementWithText("weight", weight));

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }

}
