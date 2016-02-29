package server.model;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
 * save properties in XML file.
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlMessageServer extends XmlMessage {
    private static final int    DEFAULTPORT = 1506;
    private static final String NAMEOFFILE  = "MessengerConf.xml";
    private static final String ROOTNAME    = "Preference";
    private static final String INCLUDLOG   = "logger";
    private static final String LEVELLOG    = "levelLogger";
    private static final String SERVERGUI   = "serverGUI";
    private static final String NAMEPORT    = "Port";
    private static       Logger LOG         = Logger.getLogger(XmlMessageServer.class);

    /**
     * Method for create default parameters.
     * @return new ConfigParameters with default value
     */
    private static ConfigParameters setDefaultParameters() {
        ConfigParameters conf = new ConfigParameters();
        conf.setGUI(false);
        conf.setPort(DEFAULTPORT);
        conf.setLog(true);
        conf.setLevelLog(String.valueOf(LogManager.getRootLogger().getLevel()));

        return conf;
    }

    /**
     * Method that set default value for the specific parameter.
     * @param conf    class type of ConfigParameters for set value.
     * @param command is parameter for set value.
     * @return        class type of ConfigParameters.
     */
    private static ConfigParameters setDefaultParameters(ConfigParameters conf, String command) {
        if (command.equals(NAMEPORT)) {
            conf.setPort(DEFAULTPORT);
        }

        if (command.equals(LEVELLOG)) {
            conf.setLevelLog(String.valueOf(LogManager.getRootLogger().getLevel()));
        }

        return conf;
    }

    /**
     * Method for read properties.
     * @return true if read ok.
     */
    protected synchronized static ConfigParameters loadProperties() {
        boolean notFoundNode = false;
        paramLangXML();
        Document document = null;
        ConfigParameters conf = setDefaultParameters();

        // try parse the document.
        try {
            document = builder.parse(new File(NAMEOFFILE));
        } catch (IOException | SAXException e) {
            try {
                LOG.error("Configuration file do not have required parameters, or file did not find. " +
                        "Write default parameters.");
                writeProperties(conf);
                return  conf;
            } catch (TransformerException | FileNotFoundException e1) {
                LOG.error(e1);
            }
        }

        document.getDocumentElement().normalize();

        // Log parameters.
        String log;
        if ((log = readChild(document, INCLUDLOG)) != null) {
            if (log.equalsIgnoreCase("true")) {
                conf.setLog(true);
                String level;

                if ((level = readChild(document, LEVELLOG)) != null) {
                    if (setLevelLog(level)) {
                        conf.setLevelLog(level);
                    }
                } else {
                    LOG.info("Configure file do not have '" + LEVELLOG + "', it will add with default value.");
                    setDefaultParameters(conf, LEVELLOG);
                    notFoundNode = true;
                }
            } else {
                if (log.equalsIgnoreCase("false")) {
                    conf.setLog(false);
                    LogManager.getRootLogger().setLevel(Level.OFF);
                } else {
                    LOG.error("MessengerConf.xml in 'logger' has mistake, it must be 'true' or 'false'. " +
                            "Use default parameter.");
                }
            }
        } else {
            LOG.info("Configure file do not have '" + INCLUDLOG + "', it will add with default value.");
            setDefaultParameters(conf, LEVELLOG);
            notFoundNode = true;
        }

        String parameter;

        // parameter of server GUI
        if ((parameter = readChild(document, SERVERGUI)) != null) {
            if (parameter.equalsIgnoreCase("true") || parameter.equalsIgnoreCase("false") ) {
                conf.setGUI(Boolean.parseBoolean(parameter.toLowerCase()));
            } else {
                LOG.error("MessengerConf.xml in 'serverGUI' has mistake, it must be 'true' or 'false'. " +
                        "Use default parameters.");
            }
        } else {
            LOG.info("Configure file do not have '" + SERVERGUI + "', it will add with default value.");
            notFoundNode = true;
        }

        // read port.
        if ((parameter = readChild(document, NAMEPORT)) != null) {
            try {
                int port = Integer.parseInt(parameter);
                if (port  < 1024) {
                    throw null;
                }

                conf.setPort(port);
            } catch (NullPointerException | NumberFormatException e) {
                LOG.error("MessengerConf.xml in 'Port' has mistake, it must be number, and > 0.");
                conf.setPort(DEFAULTPORT);
            }
        } else {
            LOG.error("Configure file do not have 'PORT', it will add with default value " + DEFAULTPORT + ".");
            setDefaultParameters(conf, NAMEPORT);
            notFoundNode = true;
        }

        if (notFoundNode) {
            try {
                writeProperties(conf);
            } catch (TransformerException | FileNotFoundException e) {
                LOG.error(e);
            }
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
                LOG.error("MessengerConf.xml in 'levelLogger' has mistakes. Use default parameter.");
                return false;
        }
        return true;
    }

    /**
     * Write default properties.
     * @throws TransformerException if there was error of parse.
     * @throws FileNotFoundException if file did not find.
     */
    protected synchronized static void writeProperties(ConfigParameters conf) throws TransformerException, FileNotFoundException {
        paramLangXML();
        Document doc = builder.newDocument();
        Element RootElement = doc.createElement(ROOTNAME);

        //include log
        writeChild(RootElement, doc, INCLUDLOG, String.valueOf(conf.isLog()));

        //log level
        writeChild(RootElement, doc, LEVELLOG, conf.getLevelLog());

        //server's GUI
        writeChild(RootElement, doc, SERVERGUI, String.valueOf(conf.isGUI()));

        //server's port
        writeChild(RootElement, doc, NAMEPORT, String.valueOf(conf.getPort()));

        // add in XML
        doc.appendChild(RootElement);
        Transformer t=  TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(NAMEOFFILE)));
    }

}
