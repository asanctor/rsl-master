package rsl.util.contentgenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import rsl.util.contentgenerator.rsl_classes.RslEntity;
import rsl.util.contentgenerator.rsl_classes.RslLink;
import rsl.util.contentgenerator.rsl_classes.RslResource;
import rsl.util.contentgenerator.rsl_classes.RslSelector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class XMLContentGenerator {

    public static String getPresentationsAsXML(int amount)
    {

        ContentGenerator gen = new ContentGenerator();
        Set<RslEntity> entities = gen.createPresentations(amount);

        Set<Node> resources = new HashSet<Node>();
        Set<Node> selectors = new HashSet<Node>();
        Set<Node> links = new HashSet<Node>();

        for(RslEntity en: entities) {
            if (RslResource.class.isAssignableFrom(en.getClass())) {
                resources.add(en.toXML());
            } else if (RslSelector.class.isAssignableFrom(en.getClass())) {
                selectors.add(en.toXML());
            } if (RslLink.class.isAssignableFrom(en.getClass())) {
                links.add(en.toXML());
            }
        }

        String xml = makeXMLDocument("entities", resources, selectors, links);
        return xml;
    }

    private static String makeXMLDocument(String rootName, Set<Node> resources, Set<Node> selectors, Set<Node> links)
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement(rootName);

        Element resourceRoot = doc.createElement("resources");
        for (Node node: resources)
        {
            Node n = doc.adoptNode(node);
            resourceRoot.appendChild(n);
        }
        root.appendChild(resourceRoot);

        Element selectorRoot = doc.createElement("selectors");
        for (Node node: selectors)
        {
            Node n = doc.adoptNode(node);
            selectorRoot.appendChild(n);
        }
        root.appendChild(selectorRoot);

        Element linkRoot = doc.createElement("links");
        for (Node node: links)
        {
            Node n = doc.adoptNode(node);
            linkRoot.appendChild(n);
        }
        root.appendChild(linkRoot);

        doc.appendChild(root);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        DOMSource source = new DOMSource(doc);
        StringWriter sw = new StringWriter();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, new StreamResult(sw));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }
}
