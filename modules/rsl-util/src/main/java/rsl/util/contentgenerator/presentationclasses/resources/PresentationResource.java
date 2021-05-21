package rsl.util.contentgenerator.presentationclasses.resources;

import org.fluttercode.datafactory.impl.DataFactory;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.RandomTextFix;
import rsl.util.contentgenerator.XMLHelper;
import rsl.util.contentgenerator.rsl_classes.RslResource;

import java.util.ArrayList;
import java.util.Arrays;

public class PresentationResource extends RslResource {

    public String title = "";
    public String author = "";
    public String theme = "";

    public PresentationResource()
    {
        this(0,0);
    }

    public PresentationResource(long creatorID, long creationTimeStamp)
    {
        super(creatorID, creationTimeStamp);
        DataFactory df = new DataFactory();
        final String[] themeValues = {"VUB", "NewVUB", "Pretty", "default"};
        final String[] nameValues = {"Reinout Roels", df.getFirstName() + " " + df.getLastName(), df.getFirstName() + " " + df.getLastName(), df.getFirstName() + " " + df.getLastName(), df.getFirstName() + " " + df.getLastName()};

        this.author = df.getItem(nameValues);
        this.title = RandomTextFix.getRandomText(5, 50);
        this.theme = df.getItem(themeValues);
    }

    public Node[] getXMLNodes()
    {
        ArrayList<Node> fieldNodes = new ArrayList<>();

        fieldNodes.add(XMLHelper.createElementWithText("title", title));
        fieldNodes.add(XMLHelper.createElementWithText("author", author));
        fieldNodes.add(XMLHelper.createElementWithText("theme", theme));

        fieldNodes.addAll(Arrays.asList(super.getXMLNodes()));
        return fieldNodes.toArray(new Node[fieldNodes.size()]);
    }
}
