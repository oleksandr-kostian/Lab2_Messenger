package client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
    ObjectInputStream           fromServer;
    ObjectOutputStream          toServer;
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
            toServer = new ObjectOutputStream(connect.getOutputStream());
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
            new Thread(this).start();

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



    public void sendMessage(String message) {
        /*
        try {
            XmlMessage.writeXMLinStream(aut, connect.getOutputStream());

        }
        catch (javax.xml.transform.TransformerException e1) {
            System.out.println(" TransformerException " + e1);

        }
        catch(IOException e) {
            System.out.println("Exception writing to server: " + e);
            //logger.error("IOException writing to server." + e);
        }
       */
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

    public static void main(String[] args)throws IOException, ParseException {
        String serverAddress = "localhost";
        Controller client = new Controller(serverAddress);
        client.connectToServer();
      //  client.sendMessage();

    }

}
