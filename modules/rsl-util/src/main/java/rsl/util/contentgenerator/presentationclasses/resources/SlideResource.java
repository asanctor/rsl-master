package rsl.util.contentgenerator.presentationclasses.resources;

import org.fluttercode.datafactory.impl.DataFactory;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.RandomTextFix;
import rsl.util.contentgenerator.XMLHelper;
import rsl.util.contentgenerator.rsl_classes.RslResource;

import java.util.ArrayList;
import java.util.Arrays;

public class SlideResource extends RslResource {

    public String title = "";

    public SlideResource()
    {
        this(0,0);
    }

    public SlideResource(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
        DataFactory df = new DataFactory();
        this.title = RandomTextFix.getRandomText(5, 50);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.add(XMLHelper.createElementWithText("title", title));

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }

}
