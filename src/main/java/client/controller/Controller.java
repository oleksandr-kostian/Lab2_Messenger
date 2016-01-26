package client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * Client's controller
 * @author Veleri Rechembei
 * @version %I%, %G%
 */
public class Controller implements Runnable {

    private String hostName;
    final private int PORT;
    private Socket connect;
    private String myUser;
    ObjectInputStream fromServer;
    ObjectOutputStream toServer;
   // private static final Logger logger = Logger.getLogger(Controller.class);
     //ClientGUI = gui

    public Controller(String hostName, int port, String myUser) {
        this.hostName = hostName;
        this.PORT = port;
        this.myUser=myUser;

    }

    public boolean connectToServer() {
        try {
            connect = new Socket(hostName, PORT);
            System.out.println("Connected: " + connect);
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
        try{
            fromServer = new ObjectInputStream(connect.getInputStream());
            toServer = new ObjectOutputStream(connect.getOutputStream());
            new Thread(this).start();

        }
        catch (IOException e) {
            //  logger.error(e);
            System.out.println("Exception creating new Input/output Streams: " + e);
            return false;
        }
        try
        {
            toServer.writeObject(this.getMyUser());
        }
        catch (IOException eIO) {
            System.out.println("Exception doing login : " + eIO);
            closeServer();
            return false;
        }
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
       /* try {
            toServer.writeObject(message);
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
        while (true) {
               try {
                String message = (String)fromServer.readObject();

                // + print message in GUI
            }
        catch (IOException e) {
            System.out.println(e.getMessage() + e);

        }
        catch(ClassNotFoundException e2) {
            System.out.println(e2.getMessage() + e2);
        }
           }
    }


    public String getMyUser(){
        return this.myUser;
    }
    public void setMyUser(String user){
        this.myUser=user;
    }

    public static void main(String[] args)throws IOException, ParseException {
        String serverAddress = "localhost";
        Controller client = new Controller(serverAddress,994,"someUser");
        client.connectToServer();

    }

}
