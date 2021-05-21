package rsl.util.contentgenerator.presentationclasses.resources;

import org.fluttercode.datafactory.impl.DataFactory;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.XMLHelper;
import rsl.util.contentgenerator.rsl_classes.RslResource;

import java.util.ArrayList;
import java.util.Arrays;

public class BulletlistResource extends RslResource {

    public String bulletStyle = "";

    public BulletlistResource()
    {
        this(0,0);
    }

    public BulletlistResource(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
        DataFactory df = new DataFactory();
        final String[] bulletStyleValues = {"round", "square", "default"};
        this.bulletStyle = df.getItem(bulletStyleValues);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.add(XMLHelper.createElementWithText("bulletStyle", bulletStyle));

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }

}
