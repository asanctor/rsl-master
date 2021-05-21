package rsl.util.contentgenerator.presentationclasses.resources;

import org.fluttercode.datafactory.impl.DataFactory;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.XMLHelper;
import rsl.util.contentgenerator.rsl_classes.RslResource;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageResource extends RslResource {

    public String path = "";
    public String encoding = "";
    public int height = 0;
    public int width = 0;

    public ImageResource()
    {
        this(0,0);
    }

    public ImageResource(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
        DataFactory df = new DataFactory();
        this.path = df.getRandomChars(5, 50);
        this.encoding = df.getRandomChars(3, 10);
        this.height = df.getNumberBetween(1, 1000);
        this.width = df.getNumberBetween(1, 1000);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.add(XMLHelper.createElementWithText("path", path));
        fieldNodes.add(XMLHelper.createElementWithText("encoding", encoding));
        fieldNodes.add(XMLHelper.createElementWithText("height", height));
        fieldNodes.add(XMLHelper.createElementWithText("width", width));

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }

}
