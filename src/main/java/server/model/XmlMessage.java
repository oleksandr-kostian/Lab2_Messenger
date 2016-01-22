package server.model;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.List;

/**
 * Abstract class that describes work with thread for data exchange with the users
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlMessage {
    static Logger log = Logger.getLogger(XmlMessage.class);
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

    public void writeXMLinStream(XmlSet xmlSet, OutputStream out) throws TransformerException {
        paramLangXML();

        Document doc = builder.newDocument();
        Element RootElement = doc.createElement("XmlMessage");

        // id user
        Element NameElementTitle = doc.createElement("IdUser");
        NameElementTitle.appendChild(doc.createTextNode(String.valueOf(xmlSet.getIdUser())));
        RootElement.appendChild(NameElementTitle);

        //write name of active user
        if (xmlSet.getListActiveUser() != null) {
            Element list = doc.createElement("list_active_user");
            RootElement.appendChild(list);

            List<String> l = xmlSet.getListActiveUser();

            for (String i : l) {
                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode(i));
                list.appendChild(name);
            }
        }

        // general message
        if (xmlSet.getGeneralMessage() != null) {
            NameElementTitle = doc.createElement("general_message");
            NameElementTitle.appendChild(doc.createTextNode(xmlSet.getGeneralMessage()));
            RootElement.appendChild(NameElementTitle);
        }

        // create private window
        if (xmlSet.isOpenPrivateWindow()) {
            NameElementTitle = doc.createElement("create_private_window");
            NameElementTitle.appendChild(doc.createTextNode(String.valueOf(xmlSet.getKeyPrivatDialog())));

            //write list name of user
            if (xmlSet.getListPrivatDialog() != null) {
                List<String> l = xmlSet.getListPrivatDialog();

                Element list = doc.createElement("list_private_user");
                RootElement.appendChild(list);

                for (String i : l) {
                    Element name = doc.createElement("name");
                    name.appendChild(doc.createTextNode(i));
                    list.appendChild(name);
                }
            }
        }

        //send private message
        if (xmlSet.getPrivateMessage() != null) {
            NameElementTitle = doc.createElement("private_message");
            NameElementTitle.appendChild(doc.createTextNode(xmlSet.getPrivateMessage()));
            RootElement.appendChild(NameElementTitle);
        }

        // write else preference
        if (xmlSet.getElsePreference() != null) {
            NameElementTitle = doc.createElement("else_preference");
            NameElementTitle.appendChild(doc.createTextNode(xmlSet.getElsePreference()));
            RootElement.appendChild(NameElementTitle);
        }

        // add in XML
        doc.appendChild(RootElement);
        Transformer t=  TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        //try { t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("TEST.xml")));
            t.transform(new DOMSource(doc), new StreamResult(out));
        //} catch (FileNotFoundException e) { log.debug("transform "+e); }
        //DOMSource -- представляет полученные данные в виде Document Object Model (DOM).
        //(представляется в виде древовидной структуры документа)
        //StreamResult -- "Записывает в память" преобразованный документ... к которому мы можем уже обращаться...
        //как к xml документу. А файл, мы в него выгружаем полученный результат (в StreamResult) конечного преобразования.
        //transform - метод который и позволяет преобразовывать "исходник" (текст который мы выдаём за xml)
        //в xml (древовидную структуру)
    }

    public XmlSet readXmlFromStream(OutputStream in){
        XmlSet xmlSet = new XmlSet(-1);

        return xmlSet;
    }
}
