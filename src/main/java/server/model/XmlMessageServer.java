package server.model;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * Class that describes work with thread for users and
 * save propetries in XML file
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlMessageServer extends XmlMessage {

    protected synchronized static boolean loadProperties() throws IOException, SAXException {
        paramLangXML();

        Document document = builder.parse(new File("MessengerConf.xml"));
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

}
