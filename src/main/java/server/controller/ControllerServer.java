package server.controller;



import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import server.model.*;
import server.view.ServerView;
import server.view.View;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Server's controller
 *
 * @author Veleri Rechembei
 * @version %I%, %G%
 */
public class ControllerServer extends Observable implements Server{
    private final int                       PORT=1025;
    private ServerSocket                    socket;
    private List<ServerThread>              activeUsers;
    private ModelActions                    model;
    private static final Logger             logger = Logger.getLogger(ControllerServer.class);
    private View                            serverGUI;
    private volatile boolean                finish = false;

    private static final String  USER_IS_ALREADY = "This user has already been created.";
    private static final String  ONLINE_USER = "The user is online.";
    private static final String  EXIST_USER = "User does not exist!";
    private static final String  WRONG_COMMAND = "The client is not authenticated. No token \"authentication\"  word. Please try to connect again.";
    private static final String  UNBAN =  "You was unban";
    private static final String  DELETE = "Admin deleted you.";

    /**
     * Method, that add ServerThread of client to list of active users.
     * @param activeUser ServerThread of client.
     */
    public synchronized void addActiveUser(ServerThread activeUser){
        activeUsers.add(activeUser);
        this.setChanged();
        notifyObservers();
    }

    /**
     * Method, that remove ServerThread of client from list of active users.
     * @param activeUser ServerThread of client.
     */
    public synchronized void removeActiveUser(ServerThread activeUser){
        activeUsers.remove(activeUser);
        this.setChanged();
        notifyObservers();
    }

    /**
     * Default constructor of servers controller.
     * @throws IOException if port don't build; wrong of client's socket.
     * @throws SAXException if ServerThread has mistake of xml.
     */
    public ControllerServer(ModelActions model)throws  IOException, SAXException{
        this.model = model;
        run();
    }
    /**
     * GUI constructor of servers controller.
     * @param serverGUI GUI of servers controller.
     */
    public ControllerServer(View serverGUI,ModelActions model){
        this.serverGUI = serverGUI;
        this.model = model;
        this.serverGUI.setServer(this);
    }

    /**
     * Method, that start GUI of server.
     * @return String value <code>start</code> if server is running,
     *         String value <code>stop</code>  if server is stopped.
     */
    @Override
    public String startGUI(){
        if(this.serverGUI!=null){
            if (!serverGUI.isServerStart()) {
                this.finish=false;
                new Thread(new Runnable() {
                   public void run() {
                        try {
                            model.start();
                            serverGUI.getServer().run();
                        }
                        catch (org.xml.sax.SAXException e) {
                            catchGuiException(e);
                        }
                        catch (IOException  e){
                            catchGuiException(e);
                        }
                   }
                }).start();
                serverGUI.setServerStart(true);
                return "Stop";
            }
            if (serverGUI.isServerStart()) {
                try {
                    this.stop();
                    serverGUI.setServerStart(false);
                    return ("Start");
                } catch (IOException e3) {
                   catchGuiException(e3);
                }
            }
        }
        return "";
    }
    /**
     * Registration the user on the server.
     * @param client thread of client.
     * @param login is String login of user.
     * @param password is String password of user.
     * @throws TransformerException if method send() has mistake.
     */
    public synchronized void registration (ServerThread client, String login, String password)throws TransformerException{
        User createUser = new User();
        createUser.setLogin(login);
        createUser.setPassword(password);
        createUser.setBan(false);
        createUser.setIsAdmin(false);
        if(model.addUser(createUser)) {
            client.getXmlUser().setIdUser(createUser.getId());
            client.setUser(createUser);
            client.setAuthentication(true);
            client.getXmlUser().setMessage(Preference.Successfully.name());
            client.sendMessage(Preference.Registration.name());
            this.displayInfoLog("New user: " + client.getUser().getLogin() + "  is welcome.");
            logger.debug("Create new user " + client.getUser().getLogin());
        }
        else{
            throw new IllegalArgumentException(USER_IS_ALREADY);
        }
    }

    /**
     * Authenticates the user on the server.
     * @param client thread of client.
     * @throws TransformerException if method send() has mistake.
     */
  public synchronized void authorization(ServerThread client)throws TransformerException{
      boolean online = false;
      String preference = client.getXmlUser().getPreference();
      Preference command = Preference.fromString(preference);
      List<String> data = client.getXmlUser().getList();
      long idUser = model.authorizationUser(data.get(0), data.get(1));

      switch (command){
          case Registration:
              try {
                  if (idUser == -1) {
                      registration(client, data.get(0), data.get(1));
                  }
                  else {
                      throw new IllegalArgumentException(USER_IS_ALREADY);
                  }
              }
              catch (IllegalArgumentException e){
                  client.getXmlUser().setMessage(Preference.IncorrectValue.name() + " name of user. "+USER_IS_ALREADY);
                  client.sendMessage(Preference.Registration.name());
              }
              break;
          case Authentication:
              if (idUser != -1) {
                  client.getXmlUser().setIdUser(idUser);
                  client.setUser(model.getUser(idUser));

                  for (int i = 0; i < activeUsers.size(); i++) {
                      if (activeUsers.get(i).getUser().getLogin().compareToIgnoreCase(client.getUser().getLogin()) == 0) {
                          online = true;
                          client.getXmlUser().setMessage(ONLINE_USER);
                          client.sendMessage(Preference.Authentication.name());
                          break;
                      }
                  }
                  if (!online) {
                      client.setAuthentication(true);
                      if (client.getUser().isBan()) {
                          client.getXmlUser().setMessage(Preference.Ban.name());
                      }
                      else{
                          client.getXmlUser().setMessage(Preference.Successfully.name());
                      }
                      if (client.getUser().isAdmin()) {
                          client.sendMessage(Preference.Admin.name());
                      } else {
                          client.sendMessage(Preference.Authentication.name());
                      }

                      logger.debug("Authentications user is " + client.getUser().getLogin());
                      if (client.getUser().isAdmin()) {
                          this.displayInfoLog("Admin: " + client.getUser().getLogin() + " is welcome.");
                      }
                      else {
                          this.displayInfoLog(client.getUser().getLogin() + " is welcome.");
                      }
                  }
              }
              else{
                  client.getXmlUser().setMessage(EXIST_USER);
                  client.sendMessage(Preference.Authentication.name());
              }
              break;
          default:
              client.getXmlUser().setMessage(WRONG_COMMAND);
              client.sendMessage(Preference.Authentication.name());
              break;
      }
      if(client.isAuthentication()){
          client.getXmlUser().setMessage(getDate()+" System message: user < " + client.getUser().getLogin() + " > is welcome.");
          try {
              readCommand(client, Preference.MessageForAll);
          }
          catch (TransformerException e){
            logger.error(e);
          }
          addActiveUser(client);
      }

  }

    /**
     * Method for display information on servers GUI or console, if GUI is null and on info message of lOG.
     * @param message is String information of server.
     */
    @Override
    public void displayInfoLog(String message){
        if(serverGUI!=null){
            serverGUI.display(message);
        }
        else{
            System.out.println(message);
        }
            logger.info(message);
    }

    /**
     * Method for start gracefulReload.
     */
    @Override
    public void gracefulReload(){
        this.model.gracefulReload();
    }

    /**
     * Method for write exception on servers GUI to LOG error message.
     * @param message is exception of server.
     */
    @Override
    public void catchGuiException(Exception message) {
        logger.error("Server GUI: ", message);

    }
    /**
     * Method for start work of server.
     * @throws IOException if port don't build; wrong of client's socket.
     * @throws SAXException if ServerThread has mistake of xml.
     */
    @Override
    public void run()throws IOException, SAXException{
      activeUsers = new ArrayList<>();
      this.displayInfoLog("Building to port " + this.PORT + ", please wait  ...");
      socket = new ServerSocket(PORT);
      this.displayInfoLog("Server started.");
      this.displayInfoLog("Waiting a client... ");
      while (true) {
          if(this.finish){
              return;
          }
          try {
              Socket client = socket.accept();
              logger.debug("Connection from " + client.getInetAddress().getHostName());
              this.addObserver(new ServerThread(client));
          }
          catch (SocketException e){
              continue;
          }

      }

    }
    private String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String date = ".:Time: " +formatter.format(new Date())+":.";
        return date;

    }
    /**
     * Method for stop server's controller.
     * @throws IOException if steams has error.
     */
    public void stop() throws IOException{
       this.finish=true;
        if (socket != null) {
                socket.close();
            }
        this.displayInfoLog("\n" + "Server is stopped." + "\n");
        model.stop();

    }

    /**
     * Method, that transforms HashMap<User,ServerThread> to List<String>.
     * @return String list of active users of server.
     */
  public List<String> getUserListString(){
        ArrayList<String> userList = new ArrayList<>();
      for(int i=0;i<activeUsers.size();i++){
          userList.add(activeUsers.get(i).getUser().getLogin());
      }
        return userList;

    }
    /**
     * Method, that reads client's preference, handles it and sends answer to client.
     * @param client thread of client.
     * @param preference preference of client's message.
     * @throws TransformerException if transformation xml to OutputStream has mistake.
     */
    public synchronized void readCommand(ServerThread client,Preference preference) throws TransformerException{

        switch (preference){

            case MessageForAll:
                String messageToChat = client.getXmlUser().getMessage();
                for(int i=0;i<activeUsers.size();i++){
                    activeUsers.get(i).getXmlUser().setMessage(messageToChat);
                    activeUsers.get(i).sendMessage(Preference.MessageForAll.name());
                }
                Model.logMessage(client.getXmlUser());
                break;

            case PrivateMessage:
                String messageToPrivateChat = client.getXmlUser().getMessage();
                List<String> userList = client.getXmlUser().getList();
                if(userList!=null) {
                    int keyDialog=client.getXmlUser().getKeyDialog();
                    List<String> privateList = new ArrayList<>();
                    privateList.add(client.getUser().getLogin());
                    privateList.addAll(userList);
                    client.sendMessage(Preference.PrivateMessage.name());
                    for(int j=0;j<activeUsers.size();j++){
                        for (int i = 0; i < userList.size(); i++) {
                            if (activeUsers.get(j).getUser().getLogin().compareToIgnoreCase(userList.get(i)) == 0) {
                                activeUsers.get(j).getXmlUser().setMessage(messageToPrivateChat);
                                activeUsers.get(j).getXmlUser().setList(privateList);
                                activeUsers.get(j).getXmlUser().setKeyDialog(keyDialog);
                                activeUsers.get(j).sendMessage(Preference.PrivateMessage.name());
                            }
                        }
                    }
                    Model.logMessage(client.getXmlUser());
                }
                else{
                    client.getXmlUser().setMessage(Preference.IncorrectValue.name());
                    client.sendMessage(Preference.PrivateMessage.name());
                }
                break;

            case Ban:
                if(client.getUser().isAdmin()) {
                    String infoFoBan = client.getXmlUser().getList().get(0);
                    if (model.setBan(infoFoBan, true)) {
                        client.getXmlUser().setMessage(Preference.Successfully.name());
                        client.sendMessage(Preference.Ban.name());
                    }
                    for(int i=0;i<activeUsers.size();i++){
                        if (activeUsers.get(i).getUser().getLogin().compareToIgnoreCase(infoFoBan) == 0) {
                                activeUsers.get(i).getUser().setBan(true);
                                activeUsers.get(i).getXmlUser().setMessage(Preference.Ban.name());
                                activeUsers.get(i).sendMessage(Preference.Ban.name());
                                break;


                        }
                    }
                }
                break;

            case UnBan:
                if(client.getUser().isAdmin()) {
                    String infoFoBan2 = client.getXmlUser().getList().get(0);
                    List<String> banUsers = model.getBanList();
                    for(int i=0;i<banUsers.size();i++){
                        if (banUsers.get(i).compareToIgnoreCase(infoFoBan2) == 0) {
                             if(model.setBan(infoFoBan2,false)) {
                                client.getXmlUser().setMessage(Preference.Successfully.name());
                                break;
                            }
                        }
                    }
                    for(int i=0;i<activeUsers.size();i++){
                        if (activeUsers.get(i).getUser().getLogin().compareToIgnoreCase(infoFoBan2) == 0) {
                                activeUsers.get(i).getUser().setBan(false);
                                activeUsers.get(i).getXmlUser().setMessage(UNBAN);
                                activeUsers.get(i).sendMessage(Preference.UnBan.name());
                                break;

                        }
                    }
                    this.displayInfoLog("Admin "+ Preference.UnBan.name()+" user:  " + infoFoBan2);
                }

                client.sendMessage(Preference.UnBan.name());
                break;

            case BanUsers:
                List<String> banUsers = model.getBanList();
                client.getXmlUser().setList(banUsers);
                client.getXmlUser().setMessage(Preference.BanUsers.name());
                client.sendMessage(Preference.BanUsers.name());
                break;

            case Edit:
                    List<String> newUser = client.getXmlUser().getList();
                    try {
                        client.getUser().setLogin(newUser.get(0));
                        client.getUser().setPassword(newUser.get(1));
                        if(!model.editUser(client.getUser())){
                            throw  new IllegalArgumentException("Not unique name!");
                        }
                        client.getXmlUser().setMessage(Preference.Successfully.name());
                        client.sendMessage(Preference.Edit.name());
                        this.displayInfoLog("Edit of user: " + client.getUser().getLogin() + " is successful. ");
                        logger.debug(Preference.Edit.name() + " user: " + client.getUser().getLogin());
                        this.setChanged();
                        this.notifyObservers();
                        break;
                    }
                    catch (IllegalArgumentException e ){
                        client.getXmlUser().setMessage(Preference.IncorrectValue.name());
                        client.sendMessage(Preference.Edit.name());
                        break;
                    }

            case Remove:
                if(client.getUser().isAdmin()){
                    String removeUser = client.getXmlUser().getList().get(0);
                    for(int i=0;i<activeUsers.size();i++){
                        if(activeUsers.get(i).getUser().getLogin().compareToIgnoreCase(removeUser)==0){
                            model.removeUser(activeUsers.get(i).getUser());
                            activeUsers.get(i).getXmlUser().setMessage(DELETE);
                            activeUsers.get(i).sendMessage(Preference.Remove.name());
                            activeUsers.get(i).close();
                            removeActiveUser(activeUsers.get(i));
                            client.getXmlUser().setMessage(Preference.Admin.name());
                            client.sendMessage(Preference.Remove.name());
                            break;

                        }
                    }

                    this.displayInfoLog("Admin remove user: " + removeUser);
                    logger.debug(Preference.Remove.name()+ " removeUser");
                }
                else{
                    model.removeUser(client.getUser());
                    removeActiveUser(client);
                    client.getXmlUser().setMessage(Preference.Successfully.name());
                    client.sendMessage(Preference.Remove.name());
                    client.close();
                    deleteObserver(client);
                    this.displayInfoLog("Server remove user:  " + client.getUser().getLogin());
                    logger.debug("Remove " + client.getUser().getLogin());
                }
                break;

            case Close:
                deleteObserver(client);
                removeActiveUser(client);
                client.close();
                this.displayInfoLog("User: " + client.getUser().getLogin() + " close.");
                client.getXmlUser().setMessage(getDate()+" System message: user < " + client.getUser().getLogin() + " > is closed.");
                readCommand(client, Preference.MessageForAll);
                break;

            default:
                client.sendMessage(Preference.IncorrectValue.name());
                break;
        }
    }

    public static void main(String[] args)throws IOException, ParseException,SAXException {
       new ControllerServer(new ServerView(), new Model());
    }

    /**
     * Class of client's thread.
     */
    public class ServerThread extends Thread implements Observer{
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
         * @throws SAXException if method start() has mistake of xml.
         */
        public ServerThread(Socket socket) throws  IOException,SAXException{
            this.socket = socket;
            this.authentication=false;
            fromClient = socket.getInputStream();
            toClient=socket.getOutputStream();
            this.setDaemon(true);
            start();
        }

        /**
         * Method for update information of list of active users and send it to client.
         * @param o class "ControllerServer"
         * @param arg object of class "ControllerServer", that was updated.
         */
        @Override
        public void update(Observable o, Object arg) {
            this.getXmlUser().setList(((ControllerServer) o).getUserListString());
            try{
                sendMessage(Preference.ActiveUsers.name());
            }
            catch (TransformerException e) {
                try{
                    readCommand(this,Preference.Close);
                }
                catch (TransformerException e1) {
                    logger.error(e1);
                }
            }
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
         * @throws SAXException if read xml.
         */
        public  void getMessage() throws IOException,SAXException{
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
                boolean isEditRepeat=false;
                while (true) {
                   if (finish) {
                       this.close();
                       return;
                   }
                    try {
                        this.getMessage();
                        if(isEditRepeat==true){
                            isEditRepeat=false;
                            continue;
                        }
                    }
                    catch (IOException e) {
                        continue;
                    }
                    if (getXmlUser() != null) {
                        if (!this.isAuthentication()) {
                            authorization(this);
                        }
                        else {
                            String preference = getXmlUser().getPreference();
                            Preference command = Preference.fromString(preference);
                           if(!finish) {
                                readCommand(this, command);
                                if (preference.equals(Preference.Edit.name())) {
                                    isEditRepeat = true;
                                }
                            }
                        }
                    }
                }
            }
            catch (SAXException e) {
                logger.error(e);
            }
            catch (TransformerException e){
                logger.error(e);
            }


        }

        /**
         * Method of send message to client.
         * @param message is a String message to client.
         * @throws TransformerException if transformation xml to OutputStream has mistake.
         */
        public void sendMessage(String message) throws TransformerException {
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

