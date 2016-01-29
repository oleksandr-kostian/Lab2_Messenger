package server.controller;


import org.apache.log4j.Logger;
import server.model.Model;
import server.model.User;
import server.model.XmlMessage;
import server.model.XmlSet;
//import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;

/**
 * Server's controller
 * @author Veleri Rechembei
 * @version %I%, %G%
 */
public class ControllerServer {
    private final int                       PORT=1025;
    private ServerSocket                    socket;
    private HashMap<User,ServerThread>      activeUser;
    private Model                           model;
    private static Logger                   logger = Logger.getLogger(ControllerServer.class);
    // private List<ServerThread>            activeUser;


    public void run() throws IOException{
            model = new Model();
            activeUser = new HashMap<>();
            logger.info("Binding to port " + this.PORT + ", please wait  ...");
            System.out.println("Binding to port " + this.PORT + ", please wait  ...");
            socket = new ServerSocket(PORT);
            logger.info("Server started: ");
            System.out.println("Server started: ");
            logger.info("Waiting a client... ");
            System.out.println("Waiting a client... ");
            //connect new client and authentication
            while (true) {
             Socket client = socket.accept();
              System.out.println("Connection from " + client.getInetAddress().getHostName());
                ServerThread newUser= new ServerThread(client);
                newUser.start();
                String preference=newUser.getXmlUser().getPreference();
                List<String> data = newUser.getXmlUser().getList();
                if(preference.compareToIgnoreCase("authentication")==0){
               int idUser= model.authorizationUser(data.get(0),data.get(1));
                    if(idUser!=-1){
                        newUser.getXmlUser().setIdUser(idUser);
                        newUser.setUser(model.getUser(idUser));
                        activeUser.put(model.getUser(idUser), newUser);
                        newUser.setAuthentication(true);
                        logger.info("Authentication is successful.");
                        logger.debug("Authentications user is" + newUser.getUser().getLogin());
                    }
                    else{
                        User createUser = new User();
                        createUser.setLogin(data.get(0));
                        createUser.setPassword(data.get(1));
                        createUser.setBan(false);
                        newUser.getXmlUser().setIdUser(createUser.getId());
                        newUser.setUser(createUser);
                        model.addUser(createUser);
                        activeUser.put(createUser, newUser);
                        newUser.setAuthentication(true);
                        logger.info("Authentication is successful. New user  created.");
                        logger.debug("Create new user" + newUser.getUser().getLogin());
                    }
                            //send message to client of active user list and data of client
                            newUser.getXmlUser().setList(getUserListString());
                            if(newUser.getUser().isBan()){
                                newUser.getXmlUser().setMessage("ban");
                            }
                            else {
                                newUser.getXmlUser().setMessage("activeUser");
                            }
                            newUser.sendMessage("authentication");

                }

                else{
                    newUser.getXmlUser().setMessage("The client is not authenticated. No token \"authentication\" word. Please try to connect again.");
                    newUser.sendMessage("authentication");
                    newUser.close();
                    throw new IllegalArgumentException("The client is not authenticated. No token \"authentication\" word.");

                }

        }


    }
    public void stop() throws IOException{
            for (User key : activeUser.keySet()) {
                activeUser.get(key).stop();
            }
            if(socket != null) socket.close();


    }
    public HashMap<User,ServerThread> getUserList(){
        return activeUser;

    }
    public List<String> getUserListString(){
        ArrayList<String> userList = new ArrayList<>();
        for (User key : activeUser.keySet()) {
            userList.add(activeUser.get(key).getUser().getLogin());
        }
        return userList;

    }
    public ArrayList getBanList(){
        ArrayList<ServerThread> banList = new ArrayList();
        for (User key : activeUser.keySet()) {
            if( activeUser.get(key).user.isBan()){
                banList.add(activeUser.get(key));
            }

        }
        return banList;

    }

    public void readCommand(ServerThread client, String command){
        if(command.compareToIgnoreCase("activeUser")==0){
            client.getXmlUser().setList(getUserListString());
            client.getXmlUser().setMessage("activeUser");
            client.sendMessage("activeUser");
        }
        if(command.compareToIgnoreCase("private")==0){
            String messageToChat = client.getXmlUser().getMessage();
            List<String> userList = client.getXmlUser().getList();
            for (User key : activeUser.keySet()) {
                for (int i = 0; i < userList.size(); i++) {
                    if (activeUser.get(key).getUser().getLogin().compareToIgnoreCase(userList.get(i))==0){
                        activeUser.get(key).getXmlUser().setMessage(messageToChat);
                        activeUser.get(key).sendMessage("private");
                    }
                }
            }

        }
        if(command.compareToIgnoreCase("all")==0){
            String messageToChat = client.getXmlUser().getMessage();
            for (User key : activeUser.keySet()) {
                activeUser.get(key).getXmlUser().setMessage(messageToChat);
                activeUser.get(key).sendMessage("all");
            }
            System.out.println("all");
        }
        if(command.compareToIgnoreCase("edit")==0){
            model.editUser(client.getUser());
            client.getXmlUser().setMessage("edit is successful.");
            client.sendMessage("edit");
        }
        if(command.compareToIgnoreCase("remove")==0){
            model.removeUser(client.getUser());
            activeUser.remove(client);
            client.getXmlUser().setMessage("remove is successful.");
            client.sendMessage("remove");
        }
        if(command.compareToIgnoreCase("ban")==0){
            //+ проверка на админа
            List<String> infoFoBan = client.getXmlUser().getList();
            for (User key : activeUser.keySet()) {
                    if (activeUser.get(key).getUser().getLogin().compareToIgnoreCase(infoFoBan.get(1)) == 0) {
                        if(infoFoBan.get(0).compareToIgnoreCase("ban")==0) {
                            activeUser.get(key).getUser().setBan(true);
                            client.getXmlUser().setMessage("ban");
                            break;
                        }
                        else{
                            activeUser.get(key).getUser().setBan(false);
                            client.getXmlUser().setMessage("ban is remove");
                            break;
                        }
                    }

            }
            client.sendMessage("ban");

        }
        if(command.compareToIgnoreCase("close")==0){
            client.close();
        }
        else{
            throw new IllegalArgumentException("Wrong command!");
        }


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
        private XmlSet xmlUser;
        boolean authentication;

        public ServerThread(Socket socket) {
            this.socket = socket;
            this.authentication=false;
        }
        public XmlSet getXmlUser() {
            return xmlUser;
        }

        public void setXmlUser(XmlSet xmlUser) {
            this.xmlUser = xmlUser;
        }
       public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
        public boolean isAuthentication() {
            return authentication;
        }

        public void setAuthentication(boolean authentication) {
            this.authentication = authentication;
        }


        @Override
        public void run() {
            try {
                while (true) {
                    fromClient = new ObjectInputStream(socket.getInputStream());
                    if (!this.isAuthentication()) {
                        this.setXmlUser(XmlMessage.readXmlFromStream(fromClient));
                    } else {

                        //try to read command from client!!!!!!!!
                        this.setXmlUser(XmlMessage.readXmlFromStream(fromClient));
                        if (getXmlUser().getIdUser() >= 0) {
                            String preference = getXmlUser().getPreference();
                            readCommand(this, preference);
                            sleep(1000);
                        }

                    }
                }
            }
            catch (java.lang.InterruptedException e2) {
            }
            catch (org.xml.sax.SAXException e1) {
                System.out.println(" SAXException.Authorization is not passed successfully. " + e1);
            }
            catch (IOException e) {
                System.out.println(" Exception reading Streams: " + e);
            }
        }
        public void sendMessage(String message){
            try {
                getXmlUser().setPreference(message);
                toClient = new ObjectOutputStream(socket.getOutputStream());
                XmlMessage.writeXMLinStream(getXmlUser(), toClient);
                toClient.flush();
               // toClient.writeObject(toClient);
            }
            catch (javax.xml.transform.TransformerException e1) {
                System.out.println(" TransformerException " + e1);

            }
            catch (IOException e) {
                System.out.println(" Exception reading Streams: " + e);

            }

        }
        public void close()  {
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

