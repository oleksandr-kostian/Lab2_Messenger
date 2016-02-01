package server.controller;


import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
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
    private static final Logger             logger = Logger.getLogger(ControllerServer.class);

    /**
     * Authenticates the user on the server.
     * @param client thread of client.
     * @throws javax.xml.transform.TransformerException if method send() has mistake.
     * throws new IllegalArgumentException if client is not authenticated. No token \"authentication\" word.
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
              logger.info("Authentication is successful.");
              logger.debug("Authentications user is" + client.getUser().getLogin());
              //System.out.println("Authentications user is" + client.getUser().getLogin());
          }
          else{
              User createUser = new User();
              createUser.setLogin(data.get(0));
              createUser.setPassword(data.get(1));
              createUser.setBan(false);
              client.getXmlUser().setIdUser(createUser.getId());
              client.setUser(createUser);
              model.addUser(createUser);
              activeUser.put(model.getUser(createUser.getId()), client);
              //activeUser.put(createUser, client);
              client.setAuthentication(true);
              logger.info("Authentication is successful. New user  created.");
              logger.debug("Create new user" + client.getUser().getLogin());
              //System.out.println("Authentications user is" + client.getUser().getLogin());
          }
          //send message to client of active user list and data of client
          client.getXmlUser().setList(getUserListString());
          if(client.getUser().isBan()){
              client.getXmlUser().setMessage("ban");
          }
          else {
              client.getXmlUser().setMessage("activeUser");
          }
          client.sendMessage("authentication");
      }
      else{
          client.getXmlUser().setMessage("The client is not authenticated. No token \"authentication\" word. Please try to connect again.");
          client.sendMessage("authentication");
          client.close();
          throw new IllegalArgumentException("The client is not authenticated. No token \"authentication\" word.");

      }
  }

    /**
     * Method for start work of server.
     * @throws IOException if port don't build; wrong of client's socket.
     * @throws org.xml.sax.SAXException if ServerThread has mistake of xml
     */
  public void run()throws IOException, org.xml.sax.SAXException{
      model = new Model();
      activeUser = new HashMap<>();
      logger.info("Building to port " + this.PORT + ", please wait  ...");
     // System.out.println("Binding to port " + this.PORT + ", please wait  ...");
      socket = new ServerSocket(PORT);
      logger.info("Server started: ");
     // System.out.println("Server started: ");
      logger.info("Waiting a client... ");
      System.out.println("Waiting a client... ");
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
           }
            if (command.compareToIgnoreCase("private") == 0) {
               String messageToChat = client.getXmlUser().getMessage();
               List<String> userList = client.getXmlUser().getList();
               for (User key : activeUser.keySet()) {
                   for (int i = 0; i < userList.size(); i++) {
                       if (activeUser.get(key).getUser().getLogin().compareToIgnoreCase(userList.get(i)) == 0) {
                           activeUser.get(key).getXmlUser().setMessage(messageToChat);
                           activeUser.get(key).sendMessage("private");
                       }
                   }
               }

           }
            if (command.compareToIgnoreCase("all") == 0) {

               String messageToChat = client.getXmlUser().getMessage();
               for (User key : activeUser.keySet()) {
                   activeUser.get(key).getXmlUser().setMessage(messageToChat);
                   activeUser.get(key).sendMessage("message to all");
               }
              // System.out.println("all");
           }
            if (command.compareToIgnoreCase("edit") == 0) {
               model.editUser(client.getUser());
               client.getXmlUser().setMessage("edit is successful.");
               client.sendMessage("edit");
           }
            if (command.compareToIgnoreCase("remove") == 0) {
                //удаление самого пользователя
               model.removeUser(client.getUser());
               activeUser.remove(client);
               client.getXmlUser().setMessage("remove is successful.");
               client.sendMessage("remove");
                //+удаление пользователя админом
           }
              if (command.compareToIgnoreCase("ban") == 0) {
               //+ проверка на админа
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
               client.sendMessage("ban");
           }
           if (command.compareToIgnoreCase("close") == 0) {
               client.close();

           }
    }

    public static void main(String[] args)throws IOException, ParseException,SAXException {
        ControllerServer cr = new ControllerServer();
        cr.run();

    }

    /**
     * Class of client's thread.
     */
    public class ServerThread extends Thread {
       private User user;
        private Socket socket; //connect with client
        InputStream fromClient;
        OutputStream toClient;
        private XmlSet xmlUser;
        boolean authentication;

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
        public void close()  {
            try {
                if(fromClient != null) {
                    fromClient.close();
                }
            }
            catch(Exception e) {logger.error(e);}
            try {
                if(toClient != null) {
                    toClient.close();
                }
            }
            catch(Exception e) {logger.error(e);}
            try{
                if(socket != null){
                    socket.close();
                }
            }
            catch(Exception e) {logger.error(e);}
        }

    }
}

