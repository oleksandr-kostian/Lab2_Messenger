package server.controller;

import server.model.User;
import server.model.XmlMessage;
import server.model.XmlSet;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by Фокстрот on 15.02.2016.
 */
public class ClientsThread extends Thread implements java.util.Observer{
    private ControllerOfServer              server;
    private User                    user;
    private Socket                  socket;
    private InputStream             fromClient;
    private OutputStream            toClient;
    private XmlSet                  xmlUser;
    boolean                         authentication;
    @Override
    public void update(Observable o, Object arg) {
        getXmlUser().setList(((ControllerOfServer) o).getUserListString());
        try {

            sendMessage(Preference.ActiveUsers.name());

        } catch (TransformerException e) {
            // logger.error(e);
        }
    }

    /**
     * Constructor of class.
     * @param socket socket of client.
     * @throws IOException if socket has mistake.
     * @throws org.xml.sax.SAXException if method start() has mistake of xml.
     */
    public ClientsThread(Socket socket,ControllerOfServer server) throws  IOException,org.xml.sax.SAXException{
        this.server = server;
        this.socket = socket;
        this.authentication=false;
        fromClient = socket.getInputStream();
        toClient=socket.getOutputStream();
        this.setDaemon(true);
        start();
    }


    /**
     * Method that return XmlSet of user.
     * @return XmlSet of user.
     */
    public XmlSet getXmlUser() {
        return xmlUser;
    }

    /**
     * Method for set XmlSet of user.
     * @param xmlUser XmlSet of user.
     */
    public void setXmlUser(XmlSet xmlUser) {
        this.xmlUser = xmlUser;
    }

    /**
     * Method that return user in client's thread.
     * @return user of client.
     */
    public User getUser() {
        return user;
    }

    /**
     * Method set  user in client's thread.
     * @param user user for client.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Method that return true or false for client's authentication.
     * @return  <code>true</code> if user has been authenticated.
     *          <code>false</code> if user hasn't been authenticated.
     */
    public boolean isAuthentication() {
        return authentication;
    }

    /**
     * Method for set authentication of client.
     * @param authentication <code>true</code> if user has been authenticated.
     *          <code>false</code> if user hasn't been authenticated.
     */
    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    /**
     * Method of read message from client to server.
     * @throws IOException of read line.
     */
    public  void getMessage() throws IOException,org.xml.sax.SAXException,java.lang.InterruptedException{
        BufferedReader is = new BufferedReader(new InputStreamReader(fromClient));
        StringBuffer ans = new StringBuffer();
        while (true) {
            String input = is.readLine();
            ans.append(input);
            if (input == null || input.equals("</XmlMessage>")) {
                break;
            }
        }
        this.setXmlUser(XmlMessage.readXmlFromStream(new ByteArrayInputStream(ans.toString().getBytes())));


    }

    /**
     * Method that describes the action thread.
     */
    @Override
    public void run() {
        try {
            while (true) {
                if (server.isFinish()) {
                    this.close();
                    return;
                }
                //+timeout of client
                try {
                    this.getMessage();
                }
                catch (IOException e) {
                    continue;
                }
                catch (InterruptedException e) {
                    continue;
                }
                if (getXmlUser() != null) {
                    if (!this.isAuthentication()) {
                        server.authorization(this);
                    } else {
                        //try to read command from client!!!!!!!!
                        String preference = getXmlUser().getPreference();
                        Preference command = Preference.fromString(preference);
                        server.readCommand(this, command);

                    }


                }
            }

        }
        catch (org.xml.sax.SAXException e) {
       //     logger.error(e);
        }
        catch (javax.xml.transform.TransformerException e){
        //    logger.error(e);
        }

    }

    /**
     * Method of send message to client.
     * @param message is a String message to client.
     * @throws javax.xml.transform.TransformerException if transformation xml to OutputStream has mistake.
     */
    public void sendMessage(String message) throws javax.xml.transform.TransformerException {
        getXmlUser().setPreference(message);
        XmlMessage.writeXMLinStream(getXmlUser(), toClient);
    }

    /**
     * Method, that try to close ServerThread of client.
     */
    public void close() {
        try {
            if (fromClient != null) {
                fromClient.close();
            }
            if (toClient != null) {
                toClient.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e){
        //    logger.error(e);
        }
    }



}

