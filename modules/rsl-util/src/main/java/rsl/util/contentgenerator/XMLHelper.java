package rsl.util.contentgenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLHelper {

    private static DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder docBuilder = null;
    private static Document doc = null; //docBuilder.newDocument();
    static {
        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    public static Node createElement(String name)
    {
        return doc.createElement(name);
    }

    public static Node createElementWithText(String name, int value)
    {
        return createElementWithText(name, String.valueOf(value));
    }

    public static Node createElementWithText(String name, long value)
    {
        return createElementWithText(name, String.valueOf(value));
    }

    public static Node createElementWithText(String name, String value)
    {
        Element el = doc.createElement(name);
        el.appendChild(doc.createTextNode(value));
        return el;
    }
}
