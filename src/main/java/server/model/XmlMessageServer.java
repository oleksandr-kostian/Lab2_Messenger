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
    protected synchronized static ConfigParameters loadProperties() throws  SAXException {
        paramLangXML();

        Document document = null;
        try {
            document = builder.parse(new File("MessengerConf.xml"));
        } catch (IOException e) {
            try {
                ConfigParameters conf = new ConfigParameters();
                conf.setGUI(false);

                writeProperties(conf);
                return conf;
            } catch (TransformerException | FileNotFoundException e1) {
                LOG.error(e1);
            }
        }
        document.getDocumentElement().normalize();

        NodeList nList = document.getElementsByTagName("logger");
        Node node = nList.item(0);
        String log  = node.getTextContent();

        if (log.equalsIgnoreCase("true")) {
            nList = document.getElementsByTagName("levelLogger");
            node = nList.item(0);

            String level  = node.getTextContent();
            if (!setLevelLog(level)) {
                LOG.error("MessengerConf.xml in 'levelLogger' has mistake.");
            }
        } else {
            if (log.equalsIgnoreCase("false")) {
                LogManager.getRootLogger().setLevel(Level.OFF);
            } else {
                LOG.error("MessengerConf.xml in 'logger' has mistake, it must be 'true' or 'false'");
            }
        }

        ConfigParameters conf = new ConfigParameters();

        nList = document.getElementsByTagName("serverGUI");
        node = nList.item(0);
        String parameter = node.getTextContent();

        if (parameter.equalsIgnoreCase("true") || parameter.equalsIgnoreCase("false") ) {
            conf.setGUI(Boolean.parseBoolean(parameter.toLowerCase()));
        } else {
            LOG.error("MessengerConf.xml in 'serverGUI' has mistake, it must be 'true' or 'false'");
        }
        return conf;
    }

    /**
     * Method that sets level of logging.
     * @param level is String with level.
     * @return <code>true</code> if level sets success,
     *         <code>false</code> if level do not sets.
     */
    private static boolean setLevelLog(String level) {
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
                LOG.error("MessengerConf.xml in 'levelLogger' has mistakes");
                return false;
        }
        return true;
    }

    /**
     * write default properties.
     * @throws TransformerException if there was error of parse.
     * @throws FileNotFoundException if file did not find.
     */
    protected synchronized static void writeProperties(ConfigParameters conf) throws TransformerException, FileNotFoundException {
        paramLangXML();
        Document doc = builder.newDocument();
        Element RootElement = doc.createElement("Preference");

        Element NameElementTitle = doc.createElement("logger");
        NameElementTitle.appendChild(doc.createTextNode("TRUE"));
        RootElement.appendChild(NameElementTitle);

        NameElementTitle = doc.createElement("levelLogger");
        NameElementTitle.appendChild(doc.createTextNode(String.valueOf(LogManager.getRootLogger().getLevel())));
        RootElement.appendChild(NameElementTitle);

        //server's GUI
        NameElementTitle = doc.createElement("serverGUI");
        NameElementTitle.appendChild(doc.createTextNode(String.valueOf(conf.isGUI())));
        RootElement.appendChild(NameElementTitle);

        // add in XML
        doc.appendChild(RootElement);
        Transformer t=  TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("MessengerConf.xml")));
    }

}
