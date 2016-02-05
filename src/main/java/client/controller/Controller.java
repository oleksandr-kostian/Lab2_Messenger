package client.controller;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import server.controller.ControllerServer;
import server.model.XmlMessage;
import server.model.XmlSet;

/**
 * Client's controller
 * @author Veleri Rechembei
 * @version %I%, %G%
 */
public class Controller implements Runnable {

    private String              hostName;
    final private int           PORT=1025;
    private Socket              connect;
    private String              myUser;
    private InputStream         fromServer;
    private OutputStream        toServer;
    private XmlSet              UserXml;
   // private static final Logger logger = Logger.getLogger(Controller.class);
     //ClientGUI = gui

    public Controller(String hostName) {
        this.hostName = hostName;

    }

    public boolean connectToServer() {
        try {
            connect = new Socket(hostName, PORT);
            //fromServer = new ObjectInputStream(connect.getInputStream());
            System.out.println("Connected: " + connect);
            toServer = connect.getOutputStream();
            fromServer = connect.getInputStream();

            //fromServer = new ObjectInputStream(connect.getInputStream());
           // sendMessage("authentication");
            //logger.info("Connected: " + connect);
        }
        catch (UnknownHostException uhe) {
            // logger.error("Host unknown: " + uhe.getMessage());
            System.out.println("Host unknown: " + uhe.getMessage());
            return false;
        }
        catch (IOException e) {
            //  logger.error(e);
            System.out.println("Unexpected exception: " + e.getMessage());
            return false;
        }
         //   new Thread(this).start();

     return true;

    }

    public void closeServer() {
        try {
            if(fromServer != null) fromServer.close();
        }
        catch(Exception e) {/*logger.error(e);*/}
        try {
            if(toServer != null) toServer.close();
        }
        catch(Exception e) {/*logger.error(e);*/}
        try{
            if(connect != null) connect.close();
        }
        catch(Exception e) {/*logger.error(e);*/}

        // inform the client GUI
        /*if(gui != null)
            gui.connectionFailed();
        */
    }

    public static boolean pingServer(InetAddress serAddress, int port,  int timeout) {
        //  logger.info("Ping Server.");
        System.out.println("Ping Server.");
        Socket pingSocket = new Socket();
        Exception exception = null;
        try{
            pingSocket.connect(new InetSocketAddress(serAddress,port),timeout);
            System.out.println("ping...");
        }
        catch (IOException e) {
            //logger.error("IOException ping server." + e);
            System.out.println("Exception ping server: " + e);

        }
        finally {
            try {
                pingSocket.close();
            } catch (IOException e) {
                System.out.println("socket.close: " + e);
                // logger.error("IOException socket.close." + e);
            }
        }

        if(exception==null){
            return true;
        }
        else{
            return false;
        }

    }

    public void setUserXml(XmlSet userXml) {
        UserXml = userXml;
    }

    public XmlSet getUserXml() {
        return UserXml;
    }

    public void getMessage(){
        try {

            BufferedReader is = new BufferedReader(new InputStreamReader(fromServer));
            StringBuffer ans = new StringBuffer();
            while (true) {
                String input = is.readLine();
                ans.append(input);
                if (input == null || input.equals("</XmlMessage>")) {
                    break;
                }
            }
            this.setUserXml(XmlMessage.readXmlFromStream(new ByteArrayInputStream(ans.toString().getBytes())));
        } catch (org.xml.sax.SAXException e1) {
            System.out.println(" SAXException.Authorization is not passed successfully. " + e1);
        } catch (IOException e) {
            System.out.println(" Exception reading Streams: " + e);
        }
    }



    public void sendMessage(XmlSet xml, String message) {
        try {
            xml.setPreference(message);

            XmlMessage.writeXMLinStream(xml, toServer);


        }
        catch (javax.xml.transform.TransformerException e1) {
            System.out.println(" TransformerException " + e1);
        }
    }
    public void displayToChat(String message){

    }
    public void viewActiveUser() {

    }

   // @Override
    public void run() {

      /*  while (true) {
               try {
                  XmlMessage.readXmlFromStream(connect.getInputStream());

                // + print message in GUI
            }
        catch (IOException e) {
            System.out.println(e.getMessage() + e);

        }
        catch(ClassNotFoundException e2) {
            System.out.println(e2.getMessage() + e2);
        }
           }
        */
    }


    public String getMyUser(){
        return this.myUser;
    }
    public void setMyUser(String user){
        this.myUser=user;
    }

    public static void main(String[] args) throws IOException, ParseException, SAXException {
        ControllerServer controllerServer = new ControllerServer();
      //  client.sendMessage();

    }

}
