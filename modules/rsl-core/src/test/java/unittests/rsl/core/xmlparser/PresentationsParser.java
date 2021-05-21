package unittests.rsl.core.xmlparser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import rsl.core.EntityFactory;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.util.Log;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresentationsParser extends DefaultHandler {

    final private List<String> sections = Arrays.asList("resources", "selectors", "links");

    final private List<String> resources = Arrays.asList("PresentationResource", "SlideResource", "ImageResource", "TextResource", "BulletlistResource");
    final private List<String> selectors = Arrays.asList("ImageSelector");
    final private List<String> links = Arrays.asList("StructuralLink");

    final private List<String> intFields = Arrays.asList("x", "y", "width", "height");
    final private List<String> longFields = Arrays.asList("creatorID", "editorID", "creationTimestamp", "editTimestamp");


    private RslEntity obj = null;
    private String xmlSection = ""; // resources, selectors or links
    private int currentEntityID = 0;
    private StringBuilder currentTagContent = new StringBuilder();
    private Map<Integer, RslEntity> processedEntities = new HashMap<>();

    public static RslEntity[] parseXML(File xmlFile)
    {
        try {
            String xml = FileUtils.readFileToString(xmlFile, "UTF-8");
            return parseXML(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RslEntity[0];
    }

    public static RslEntity[] parseXML(String xml)
    {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            PresentationsParser parseHandler = new PresentationsParser();
            InputStream in = IOUtils.toInputStream(xml, "UTF-8");
            saxParser.parse(in, parseHandler);
            in.close();
            return parseHandler.getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RslEntity[0];
    }

    public RslEntity[] getResults()
    {
        return processedEntities.values().toArray(new RslEntity[processedEntities.size()]);
    }

    private String getModelForEntity(String entityName)
    {
        final List<String> mindxpres = Arrays.asList("PresentationResource", "SlideResource", "BulletlistResource", "StructuralLink");
        final List<String> shared = Arrays.asList("ImageResource", "TextResource", "ImageSelector");

        if(mindxpres.contains(entityName))
        {
            return "MindXpres";
        }

        if(shared.contains(entityName))
        {
            return "shared";
        }

        return "";
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String elName = qName;


        // we read the root tag, ignore that
        if(elName.equals("entities")){return;}

        // we read one of the 3 section tags, dividing content in resource, selector and link entities
        if(sections.contains(elName))
        {
            xmlSection = elName;
            return;
        }

        // if its a sources or targets list, ignore
        if(elName.equals("sources") || elName.equals("targets")){return;}

        // if its a source for a link, ignore, those are handled when the tag is closed
        if(xmlSection.equals("links") && elName.equals("source")){ return;}

        // if its a target for a link, ignore, those are handled when the tag is closed
        if(xmlSection.equals("links") && elName.equals("target")){ return;}

        boolean isEntityRootTag = false;
        String model = getModelForEntity(elName);
        switch(xmlSection)
        {
            case "resources":
                if(!model.equals(""))
                {
                    obj = EntityFactory.createResource(model, elName);
                    isEntityRootTag = true;
                }
                break;

            case "selectors":
                if(!model.equals(""))
                {
                    obj = EntityFactory.createSelector(model, elName);
                    isEntityRootTag = true;
                }
                break;

            case "links":
                if(!model.equals(""))
                {
                    System.out.println("model: " + model);
                    obj = EntityFactory.createLink(model, elName);
                    isEntityRootTag = true;
                }
                break;
        }

        if(isEntityRootTag)
        {
            currentEntityID = Integer.parseInt(attributes.getValue("id"));
            return;
        }


        // if we get this far, it must be one of the fields
        // reset the content stringbuilder which will be filled in the characters() method later
        currentTagContent.setLength(0);

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        String currentNodeValue = currentTagContent.toString().trim();
        currentTagContent.setLength(0);

        String elName = qName;

        // if it's a root or section tag, ignore
        if(elName.equals("entities") || sections.contains(elName)){return;}

        // if its a sources or targets list, ignore
        if(elName.equals("sources") || elName.equals("targets")){return;}

        // handle link source
        if(xmlSection.equals("links") && (elName.equals("source") || elName.equals("target"))){
            RslLink l = (RslLink) obj;
            int id = Integer.valueOf(currentNodeValue);
            RslEntity referencedEntitity = processedEntities.getOrDefault(id, null);
            if(referencedEntitity != null)
            {
                if(elName.equals("source")) {
                    l.addSource(referencedEntitity);
                }

                if(elName.equals("target")) {
                    l.addTarget(referencedEntitity);
                }
            }else{
                Log.info("Unfound ref: " + id);
            }
            return;
        }

        // we finished processing an entity, add it to the list
        if(resources.contains(elName) || selectors.contains(elName) || links.contains(elName))
        {
            processedEntities.putIfAbsent(currentEntityID, obj);
            return;
        } else { // its a field
            String methodName = "set" + StringUtils.capitalize(elName);
            if(intFields.contains(elName)) {
                obj.invokeMethod(methodName, Integer.parseInt(currentNodeValue));
            }else if(longFields.contains(elName)) {
                obj.invokeMethod(methodName, Long.parseLong(currentNodeValue));
            }else{
                obj.invokeMethod(methodName, currentNodeValue);
            }
            return;
        }

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        currentTagContent.append(ch, start, length);
    }

}
