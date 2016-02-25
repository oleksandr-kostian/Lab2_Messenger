package server.model;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class that describes work with thread for users and
 * save propetries in XML file
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlMessageServer extends XmlMessage {
    private static Logger LOG = Logger.getLogger(XmlMessageServer.class);

    /**
     * Method for read properties.
     * @return true if read ok.
     * @throws SAXException if xml parse is false.
     */
    protected synchronized static boolean loadProperties() throws  SAXException {
        paramLangXML();


        Document document = null;
        try {
            document = builder.parse(new File("MessengerConf.xml"));
        } catch (IOException e) {
            try {
                writeProperties();
            } catch (TransformerException | FileNotFoundException e1) {
                LOG.error(e1);
            }
            return false;
        }
        document.getDocumentElement().normalize();

        NodeList nList = document.getElementsByTagName("logger");
        Node node = nList.item(0);
        String log  = node.getTextContent();

        if (log.equalsIgnoreCase("true")) {
            nList = document.getElementsByTagName("levelLogger");
            node = nList.item(0);
            String level  = node.getTextContent();

            switch (level.toLowerCase()) {
                case "trace": LogManager.getRootLogger().setLevel(Level.TRACE);
                    break;
                case "debug": LogManager.getRootLogger().setLevel(Level.DEBUG);
                    break;
                case "info":  LogManager.getRootLogger().setLevel(Level.INFO);
                    break;
                case "warn":  LogManager.getRootLogger().setLevel(Level.WARN);
                    break;
                case "error": LogManager.getRootLogger().setLevel(Level.ERROR);
                    break;
                case "fatal": LogManager.getRootLogger().setLevel(Level.FATAL);
                    break;
                default:
                    return false;
            }
        } else {
            if (log.equalsIgnoreCase("false")) {
                LogManager.getRootLogger().setLevel(Level.OFF);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * write default properties.
     * @throws TransformerException if there was error of parse.
     * @throws FileNotFoundException if file did not find.
     */
    protected synchronized static void writeProperties() throws TransformerException, FileNotFoundException {
        paramLangXML();
        Document doc = builder.newDocument();
        Element RootElement = doc.createElement("Preference");

        Element NameElementTitle = doc.createElement("logger");
        NameElementTitle.appendChild(doc.createTextNode("TRUE"));
        RootElement.appendChild(NameElementTitle);

        Element ElementTitle = doc.createElement("levelLogger");
        ElementTitle.appendChild(doc.createTextNode(String.valueOf(LogManager.getRootLogger().getLevel())));
        RootElement.appendChild(ElementTitle);

        // add in XML
        doc.appendChild(RootElement);
        Transformer t=  TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("MessengerConf.xml")));
    }

}
