package rsl.util.contentgenerator.presentationclasses.selectors;

import org.fluttercode.datafactory.impl.DataFactory;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.XMLHelper;
import rsl.util.contentgenerator.rsl_classes.RslSelector;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageSelector extends RslSelector {

    public long refersTo = 0;

    public int x  = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;

    public ImageSelector()
    {
        this(0,0);
    }

    public ImageSelector(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
        DataFactory df = new DataFactory();
        this.x = df.getNumberBetween(0, 1000);
        this.y = df.getNumberBetween(0, 1000);
        this.width = df.getNumberBetween(1, 1000);
        this.height = df.getNumberBetween(1, 1000);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.add(XMLHelper.createElementWithText("x", x));
        fieldNodes.add(XMLHelper.createElementWithText("y", y));
        fieldNodes.add(XMLHelper.createElementWithText("width", width));
        fieldNodes.add(XMLHelper.createElementWithText("height", height));

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }
}
