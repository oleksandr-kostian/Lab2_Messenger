package server.controller;


import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import server.model.Model;
import server.model.User;
import server.model.XmlMessage;
import server.model.XmlSet;
import server.view.ServerView;
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
    private static final Logger             logger = Logger.getLogger(ControllerServer.class);
    private ServerView                      serverGUI;

    /**
     * Default constructor of servers controller.
     */
    public ControllerServer()throws  IOException,org.xml.sax.SAXException{
        run();
    }
    /**
     * GUI constructor of servers controller.
     * @param serverGUI GUI of servers controller.
     */
    public ControllerServer(ServerView serverGUI){
        this.serverGUI = serverGUI;
    }
    
    /**
     * Authenticates the user on the server.
     * @param client thread of client.
     * @throws javax.xml.transform.TransformerException if method send() has mistake.
     */
  public synchronized void authorization(ServerThread client)throws javax.xml.transform.TransformerException{
      String preference = client.getXmlUser().getPreference();
      List<String> data = client.getXmlUser().getList();
      if (preference.compareToIgnoreCase("authentication") == 0) {
          int idUser = model.authorizationUser(data.get(0), data.get(1));
          if(idUser!=-1){
              client.getXmlUser().setIdUser(idUser);
              client.setUser(model.getUser(idUser));
              activeUser.put(model.getUser(idUser), client);
              client.setAuthentication(true);
              logger.debug("Authentications user is " + client.getUser().getLogin());
              if(client.getUser().isAdmin()){
                  this.displayInfoLog("Admin: "+client.getUser().getLogin()+ " is welcome.");
              }
              else{
                  this.displayInfoLog(client.getUser().getLogin()+ " is welcome.");
              }
          }
          else{
              User createUser = new User();
              createUser.setLogin(data.get(0));
              createUser.setPassword(data.get(1));
              createUser.setBan(false);
              createUser.setIsAdmin(false);
              client.getXmlUser().setIdUser(createUser.getId());
              client.setUser(createUser);
              model.addUser(createUser);
              activeUser.put(model.getUser(createUser.getId()), client);
              client.setAuthentication(true);
              this.displayInfoLog("New user: "+client.getUser().getLogin()+"  is welcome.");
              logger.debug("Create new user " + client.getUser().getLogin());
          }
          //send message to client of active user list and data of client
          client.getXmlUser().setList(getUserListString());
          if(client.getUser().isBan()){
              client.getXmlUser().setMessage("ban");
          }
          else {
              client.getXmlUser().setMessage("activeUser");
          }

              if(client.getUser().isAdmin()){
                  client.sendMessage("admin");
              }

              else {
                  client.sendMessage("authentication");
              }
      }
      else{
          client.getXmlUser().setMessage("The client is not authenticated. No token \"authentication\" word. Please try to connect again.");
          client.sendMessage("authentication");
          client.close();
      }
  }

    /**
     * Method for display information on servers GUI or info message of lOG.
     * @param message is String information of server.
     */
    public void displayInfoLog(String message){
        if(serverGUI!=null){
            serverGUI.display(message);
        }
            logger.info(message);

    }

    /**
     * Method for write exception on servers GUI to LOG error message.
     * @param message is exception of server.
     */
    public void catchGuiException(Exception message) {
        logger.error("Server GUI: "+ message);

    }
    /**
     * Method for start work of server.
     * @throws IOException if port don't build; wrong of client's socket.
     * @throws org.xml.sax.SAXException if ServerThread has mistake of xml
     */
  public void run()throws IOException, org.xml.sax.SAXException{
      model = new Model();
      activeUser = new HashMap<>();
      this.displayInfoLog("Building to port " + this.PORT + ", please wait  ...");
      socket = new ServerSocket(PORT);
      this.displayInfoLog("Server started.");
      this.displayInfoLog("Waiting a client... ");
      while (true) {
              Socket client = socket.accept();
              logger.debug("Connection from " + client.getInetAddress().getHostName());
              new ServerThread(client);
      }

  }
    public void stop() throws IOException{
            for (User key : activeUser.keySet()) {
                activeUser.get(key).stop();
            }
            if(socket != null) {
                socket.close();
            }
        this.displayInfoLog("\n" + "Server is stopped." + "\n");


    }
  /*  public HashMap<User,ServerThread> getUserList(){
        return activeUser;

    }
   */

    /**
     * Method, that transforms HashMap<User,ServerThread> to List<String>.
     * @return String list of active users of server.
     */
  public List<String> getUserListString(){
        ArrayList<String> userList = new ArrayList<>();
        for (User key : activeUser.keySet()) {
            userList.add(activeUser.get(key).getUser().getLogin());
        }
        return userList;

    }
   /* public ArrayList getBanList(){
        ArrayList<ServerThread> banList = new ArrayList();
        for (User key : activeUser.keySet()) {
            if( activeUser.get(key).user.isBan()){
                banList.add(activeUser.get(key));
            }

        }
        return banList;

    }
*/

    /**
     * Method, that reads client's preference, handles it and sends answer to client.
     * @param client thread of client.
     * @param command preference of client's message.
     * @throws javax.xml.transform.TransformerException if transformation xml to OutputStream has mistake.
     */
    public synchronized void readCommand(ServerThread client,String command) throws javax.xml.transform.TransformerException{
           if (command.compareToIgnoreCase("activeUser") == 0) {
               client.getXmlUser().setList(getUserListString());
               client.getXmlUser().setMessage("activeUser");
               client.sendMessage("activeUser");
               this.displayInfoLog("Send list of active user to user: "+client.getUser().getLogin());
           }
            if (command.compareToIgnoreCase("private") == 0) {

               String messageToChat = client.getXmlUser().getMessage();
               List<String> userList = client.getXmlUser().getList();
                List<String> privateList = client.getXmlUser().getList();
                privateList.add(client.getUser().getLogin());
                for(int i=0;i<userList.size();i++){
                    privateList.add(userList.get(i));
                }
                client.sendMessage("private");
               for (User key : activeUser.keySet()) {
                   for (int i = 0; i < userList.size(); i++) {
                       if (activeUser.get(key).getUser().getLogin().compareToIgnoreCase(userList.get(i)) == 0) {
                           activeUser.get(key).getXmlUser().setMessage(messageToChat);
                           activeUser.get(key).getXmlUser().setList(privateList);
                           activeUser.get(key).sendMessage("private");
                       }
                   }
               }
               // this.displayInfoLog("User: "+client.getUser().getLogin()+" send private message to users: "+userList.toString());
                logger.debug("Send private message: " +messageToChat+" to users: "+userList.toString());
           }
            if (command.compareToIgnoreCase("all") == 0) {

               String messageToChat = client.getXmlUser().getMessage();
               for (User key : activeUser.keySet()) {
                   activeUser.get(key).getXmlUser().setMessage(messageToChat);
                   activeUser.get(key).getXmlUser().setList(getUserListString());
                   activeUser.get(key).sendMessage("message to all");
               }
               // this.displayInfoLog("User: "+client.getUser().getLogin()+" send message to all. ");
                logger.debug("Send message to all: " +messageToChat);
           }
            if (command.compareToIgnoreCase("edit") == 0) {
               model.editUser(client.getUser());
               client.getXmlUser().setMessage("edit is successful.");
               client.sendMessage("edit");
                this.displayInfoLog("Edit of user: " + client.getUser().getLogin() + " is successful. ");
                logger.debug("edit user: "+client.getUser().getLogin());
           }
            if (command.compareToIgnoreCase("remove") == 0) {
                //проверка на админа
                if(client.getUser().isAdmin()){
                    String removeUser = client.getXmlUser().getList().get(0);
                    for (User key : activeUser.keySet()) {
                        if(activeUser.get(key).getUser().getLogin().compareToIgnoreCase(removeUser)==0){
                            activeUser.remove(activeUser.get(key));
                            model.removeUser(activeUser.get(key).getUser());
                        }
                    }
                    this.displayInfoLog("Admin remove user: " + removeUser);
                    logger.debug("Remove " +removeUser);
                }
                else{
                    //удаление самого пользователя
                    model.removeUser(client.getUser());
                    activeUser.remove(client);
                    this.displayInfoLog("Server remove user:  " + client.getUser().getLogin());
                    logger.debug("Remove " + client.getUser().getLogin());
                }
                client.getXmlUser().setMessage("remove is successful.");
                client.sendMessage("remove");
            }
              if (command.compareToIgnoreCase("ban") == 0) {
                  if(client.getUser().isAdmin()) {
                      List<String> infoFoBan = client.getXmlUser().getList();
                      for (User key : activeUser.keySet()) {
                          if (activeUser.get(key).getUser().getLogin().compareToIgnoreCase(infoFoBan.get(1)) == 0) {
                              if (infoFoBan.get(0).compareToIgnoreCase("ban") == 0) {
                                  activeUser.get(key).getUser().setBan(true);
                                  client.getXmlUser().setMessage("ban");
                                  break;
                              } else {
                                  activeUser.get(key).getUser().setBan(false);
                                  client.getXmlUser().setMessage("ban is remove");
                                  break;
                              }
                          }

                      }
                      this.displayInfoLog("Admin "+ client.getXmlUser().getMessage()+" user:  " + infoFoBan.get(1));
                  }

               client.sendMessage("ban");
           }
           if (command.compareToIgnoreCase("close") == 0) {
               activeUser.remove(client);
               client.close();
               this.displayInfoLog("User: " + client.getUser().getLogin() + " close.");
           }
    }

    public static void main(String[] args)throws IOException, ParseException,SAXException {
        new ControllerServer(new ServerView());
      //  ControllerServer cr = new ControllerServer();
      // cr.run();

    }

    /**
     * Class of client's thread.
     */
    public class ServerThread extends Thread {
        private User                 user;
        private Socket               socket;
        private InputStream          fromClient;
        private OutputStream         toClient;
        private XmlSet               xmlUser;
        boolean                      authentication;

        /**
         * Constructor of class.
         * @param socket socket of client.
         * @throws IOException if socket has mistake.
         * @throws org.xml.sax.SAXException if method start() has mistake of xml.
         */
        public ServerThread(Socket socket) throws  IOException,org.xml.sax.SAXException{
            this.socket = socket;
            this.authentication=false;
            fromClient = socket.getInputStream();
            toClient=socket.getOutputStream();
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
        public  void getMessage() throws IOException,org.xml.sax.SAXException{
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
                    //+timeout of client
                    try {
                        this.getMessage();
                    } catch (IOException e) {
                        continue;
                    }
                    if (getXmlUser() != null) {
                        if (!this.isAuthentication()) {
                            authorization(this);
                        } else {
                            //try to read command from client!!!!!!!!
                            String preference = getXmlUser().getPreference();

                            readCommand(this, preference);

                        }


                    }
                }
            }
            catch (org.xml.sax.SAXException e) {
                logger.error(e);
            }
            catch (javax.xml.transform.TransformerException e){
                logger.error(e);
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
                    logger.error(e);
                }
        }


    }
}

