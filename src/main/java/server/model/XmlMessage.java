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
import java.util.List;

/**
 * Abstract class that describes work with thread for data exchange with the users
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlMessage {
    static Logger          log = Logger.getLogger(XmlMessage.class);
    static DocumentBuilder builder;

    /**
     * method creates factory for work with XML
     */
    public static void paramLangXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("ParamLangXML err " + e);
        }
    }

    public void writeChild(Element RootElement, Document doc, String strTeg, String str ) {
        Element NameElementTitle = doc.createElement(strTeg);
        NameElementTitle.appendChild(doc.createTextNode(str));
        RootElement.appendChild(NameElementTitle);
    }

    public void writeXMLinStream(XmlSet xmlSet, OutputStream out) throws TransformerException {
        paramLangXML();

        Document doc = builder.newDocument();
        Element RootElement = doc.createElement("XmlMessage");
        // id user
        writeChild(RootElement, doc, "IdUser", String.valueOf(xmlSet.getIdUser()));

        // general message
        if (xmlSet.getMessage() != null) {
            writeChild(RootElement, doc, "messageID", String.valueOf(xmlSet.getKeyMessage()));
            writeChild(RootElement, doc, "message", xmlSet.getMessage());
        }

        //write name of active user
        if (xmlSet.getActiveUser() != null) {
            Element      list;
            Integer      count = 1;
            List<String> l     = xmlSet.getActiveUser();

            for (String name : l) {
                list = doc.createElement("list_user");
                RootElement.appendChild(list);

                list.setAttribute("id", count.toString());
                writeChild(list, doc, "name", name);
                count++;
            }
        }

        // write else preference
        if (xmlSet.getElsePreference() != null) {
            writeChild(RootElement, doc, "else_preference", xmlSet.getElsePreference());
        }

        // add in XML
        doc.appendChild(RootElement);
        Transformer t=  TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        t.transform(new DOMSource(doc), new StreamResult(out));
        //DOMSource -- представляет полученные данные в виде Document Object Model (DOM).
        //(представляется в виде древовидной структуры документа)
        //StreamResult -- "Записывает в память" преобразованный документ... к которому мы можем уже обращаться...
        //как к xml документу. А файл, мы в него выгружаем полученный результат (в StreamResult) конечного преобразования.
        //transform - метод который и позволяет преобразовывать "исходник" (текст который мы выдаём за xml)
        //в xml (древовидную структуру)
    }


    public String readChild(Document document, String strTeg) {
        NodeList nList = document.getElementsByTagName(strTeg);
        Node node = nList.item(0);
        return node.getTextContent();
    }

    public XmlSet readXmlFromStream(InputStream in) throws IOException, SAXException {
        XmlSet xmlSet = new XmlSet(-1);

        paramLangXML();
        Document document;
        document = builder.parse(in);                                       //it will test in thread
        document.getDocumentElement().normalize();

        // parsing id of user
        xmlSet.setIdUser(Integer.parseInt(readChild(document, "IdUser")));

        // parsing messageID and message
        try {
            int id = Integer.parseInt(readChild(document, "messageID"));
            xmlSet.setKeyDialog(id);
            xmlSet.setMessage(readChild(document, "message"));
        } catch (Exception e){
            log.debug("messageID" + e);
        }

        // parsing list of user
        List<String> list  = new ArrayList<String>();
        NodeList     nList = document.getElementsByTagName("list_user");
        Node nNode;

        for (int temp = 0; temp < nList.getLength(); temp++) {
            nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    list.add(eElement.getElementsByTagName("name").item(0).getTextContent());
                }
        }
        xmlSet.setActiveUser(list);

        // parsing else_preference
        try {
            xmlSet.setElsePreference(readChild(document, "else_preference"));
        } catch (Exception e){
            log.debug("else_preference"+e);
        }

        // if parsing was good return xmlSet else null
        if(xmlSet.getIdUser() != -1) {
            return xmlSet;
        } else {
            return null;
        }
    }
}
