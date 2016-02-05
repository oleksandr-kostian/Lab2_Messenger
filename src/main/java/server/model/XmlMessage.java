package server.model;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that describes work with thread for data exchange with the users
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlMessage {
    private static Logger          LOG              = Logger.getLogger(XmlMessage.class);
    private static DocumentBuilder builder;
    private static final String    ROOT_ELEMENT     = "XmlMessage";
    private static final String    ID_USER          = "IdUser";
    private static final String    ELSE_PREFERENCE  = "preference";
    private static final String    ID               = "id";
    private static final String    NAME             = "name";
    private static final String    MESSAGE          = "message";
    private static final String    DIALOG_ID        = "dialogID";
    private static final String    LIST_USER        = "list_user";

    /**
     * method create factory for work with XML.
     */
    private static  void paramLangXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error("ParamLangXML err " + e);
        }
    }

    private static void writeChild(Element RootElement, Document doc, String strTeg, String str ) {
        Element NameElementTitle = doc.createElement(strTeg);
        NameElementTitle.appendChild(doc.createTextNode(str));
        RootElement.appendChild(NameElementTitle);
    }

    /**
     * Method for write of XML in a stream
     * @param xmlSet                parameters for send
     * @param out                   is a stream
     * @throws TransformerException if xml can not transform in out
     */
    public static void writeXMLinStream(XmlSet xmlSet, OutputStream out) throws TransformerException {
        Model.logMessage(xmlSet);                  //log message!!!

        paramLangXML();

        Document doc         = builder.newDocument();
        Element  RootElement = doc.createElement(ROOT_ELEMENT);
        // id user
        writeChild(RootElement, doc, ID_USER, String.valueOf(xmlSet.getIdUser()));

        //dialogID
        if (xmlSet.getKeyDialog() != 0) {
            writeChild(RootElement, doc, DIALOG_ID, String.valueOf(xmlSet.getKeyDialog()));
        }
        // general message
        if (xmlSet.getMessage() != null) {
            writeChild(RootElement, doc, MESSAGE, xmlSet.getMessage());
        }

        //write name of active user
        if (xmlSet.getList() != null) {
            Element      elist;
            Integer      count = 1;
            List<String> list  = xmlSet.getList();

            for (String name : list) {
                elist = doc.createElement(LIST_USER);
                RootElement.appendChild(elist);

                elist.setAttribute(ID, count.toString());
                writeChild(elist, doc, NAME, name);
                count++;
            }
        }

        // write else preference
        if (xmlSet.getPreference() != null) {
            writeChild(RootElement, doc, ELSE_PREFERENCE, xmlSet.getPreference());
        }

        // add in XML
        doc.appendChild(RootElement);
        Transformer t =  TransformerFactory.newInstance().newTransformer();

        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        t.transform(new DOMSource(doc), new StreamResult(out));
    }


    /**
     * Method read TextContent of node from XML with name of child.
     * @param document is a type of Document for read.
     * @param strTeg name of child
     * @return String with text or null if node did not found.
     */
    private static String readChild(Document document, String strTeg) {
        NodeList nList = document.getElementsByTagName(strTeg);
        Node     node  = nList.item(0);
        return node == null ? null : node.getTextContent();
    }

    /**
     * Method for read of XML from a stream
     * @param in            is a stream for read
     * @return              XmlSet
     * @throws IOException  if input stream can not be parse
     * @throws SAXException if input stream can not be parse
     */
    public static  XmlSet readXmlFromStream(InputStream in) throws IOException, SAXException {
        XmlSet xmlSet = new XmlSet(-1);

        paramLangXML();
        Document document;
        document = builder.parse(in);                                       //it will test in thread!!!
        document.getDocumentElement().normalize();

        String result;

        // parsing id of user
        if ((result = readChild(document, ID_USER)) != null) {
            xmlSet.setIdUser(Integer.parseInt(result));
        }

        // parsing messageID and message
        try {
            if ((result = readChild(document, MESSAGE)) != null) {
                    xmlSet.setMessage(result);
            }

            if ((result = readChild(document, DIALOG_ID)) != null) {
                    xmlSet.setKeyDialog(Integer.parseInt(result));
            }
        } catch (Exception e){
            LOG.error("messageID and ID " + e);
            LOG.debug("message, ID " + Arrays.toString(e.getStackTrace()));
        }

        // parsing list of user
        List<String> list  = new ArrayList<>();
        NodeList     nList = document.getElementsByTagName(LIST_USER);

        if (nList != null) {
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    list.add(eElement.getElementsByTagName(NAME).item(0).getTextContent());
                }
            }
            xmlSet.setList(list);
        }

        // parsing else_preference
        try {
            if ((result = readChild(document, ELSE_PREFERENCE)) != null ){
                xmlSet.setPreference(result);
            }
        } catch (Exception e){
            LOG.debug("else_preference " + e);
        }

        // if parsing was good return xmlSet else null
        if(xmlSet.getIdUser() != -1) {
            return xmlSet;
        } else {
            return null;
        }
    }
}
