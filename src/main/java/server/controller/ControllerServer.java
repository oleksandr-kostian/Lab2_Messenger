package server.controller;


import server.model.User;
//import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
/**
 * Server's controller
 * @author Veleri Rechembei
 * @version %I%, %G%
 *
 */
public class ControllerServer {


    private String serverIP;
    // private Socket socket;
    private final int PORT=994;
    private ServerSocket socket;
    private boolean isGoing=false;
    private ArrayList<ServerThread> activeUser;
    //   private static final Logger logger = Logger.getLogger(ControllerServer.class);


    public void run() {
        try{
            isGoing=true;
            System.out.println("Binding to port " + this.PORT + ", please wait  ...");
            socket = new ServerSocket(PORT);
            System.out.println("Server started: " );
            System.out.println("Waiting a client... ");
            while (true) {

                Socket client = socket.accept();
                System.out.println("Connection from " +client.getInetAddress().getHostName() );
                // System.out.println();
                // DataOutputStream toClient = new DataOutputStream(newSocket.getOutputStream());
                ServerThread newUser=  new ServerThread(client);
                activeUser.add(newUser);
                newUser.start();

            }
        }
        catch(IOException e) {
            System.out.println("Can not bind to port " + this.PORT + ": " + e.getMessage());
        }
    }
    public void stop(){
        isGoing=false;
        try {
            for(int i=0; i<activeUser.size();i++){
                activeUser.get(i).stop();
            }
        }
        catch(Exception e) {/*logger.error(e);*/}
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {/*logger.error(e);*/}

    }
    public ArrayList<ServerThread> getUserList(){
        return activeUser;

    }

    public ArrayList getBanList(){
        ArrayList banList = new ArrayList();
        for(int i=0;i<activeUser.size();i++){
           /* if( activeUser.get(i).user.isBan()){
                banList.add(activeUser);
            }
            */
        }
        return banList;

    }



    public static void main(String[] args)throws IOException, ParseException {
        ControllerServer cr = new ControllerServer();
        cr.run();
    }

    public class ServerThread extends Thread {
        private User user;
        private Socket socket; //connect with client
        ObjectInputStream fromClient;
        ObjectOutputStream toClient;

        public ServerThread(Socket socket) {
            this.socket = socket;
            try{
                ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

                //+user Authentication

            }
            catch (IOException e) {
                System.out.println("Exception creating new Input/output Streams: " + e);

            }

        }

        @Override
        public void run() {
            boolean isGoin = true;
            while(isGoin){
                // try{
                ///read command from client!!!!!!!!
                // }
               /* catch (IOException e) {
                    System.out.println(" Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                */
            }

        }
        public void sendMessage(String message){



        }
        public void close(){
            try {
                if(fromClient != null) fromClient.close();
            }
            catch(Exception e) {/*logger.error(e);*/}
            try {
                if(toClient != null) toClient.close();
            }
            catch(Exception e) {/*logger.error(e);*/}
            try{
                if(socket != null) socket.close();
            }
            catch(Exception e) {/*logger.error(e);*/}
        }


    }
}

