package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.view.*;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import server.controller.Preference;
import server.model.XmlMessage;
import server.model.XmlSet;
import javax.swing.*;

/**
 * Client's controller
 * @author Veleri Rechembei, Slavik Miroshnychenko
 * @version %I%, %G%
 */
public class Controller extends Thread implements ControllerActionsClient {

    private static final Logger logger = Logger.getLogger(Controller.class);
    private static final String  ONLINE_USER = "The user is online.";
    private static final String  EXIST_USER = "User does not exist!";
    private static final String  WRONG_COMMAND = "The client is not authenticated. No token \"authentication\"  word. Please try to connect again.";
    private static final String  UNBAN =  "You was unban";
    private static final String  BAN = "You was ban";
    private static final String  REMOVE ="You was remove";
    private static final String  DELETE = "Admin deleted you.";
    private static final String  REMOVE_ADMIN = "Removed is successfully";
    private static final String  EDIT = "Edit is successfully";
    private static final String  BAN_ADMIN = "Ban is successfully";
    private static final String  UNBAN_ADMIN = "UnBan is successfully";
    private static final String  INCORECT_REGISTRATION = "IncorrectValue name of user. This user has already been created.";
    private static final String  ENTER_TO_PRIVATE = "invites you to a private chat, are you want to enter?";
    private static final String  SURE = "Are you sure?";


    private int PORT = 1506;
    private String hostName = "localhost";
    private Socket connect;
    private String myUser;
    private List<String> activeUsers = new ArrayList<>();
    private InputStream fromServer;
    private OutputStream toServer;
    private XmlSet userXml;
    private XmlSet userSet;
    volatile private boolean ban;
    volatile private boolean admin;
    volatile private boolean close;
    volatile private boolean serverDown;
    private boolean authentication;
    private List<String> banUsers;
    private List<ChatView> views = new ArrayList<>();
    private boolean isConnect;
    public Controller() {
        isConnect = connectToServer();
    }

    public boolean connectToServer() {
        try {
            connect = new Socket(hostName, PORT);
            logger.info("Connected: " + connect);
            toServer = connect.getOutputStream();
            fromServer = connect.getInputStream();
        } catch (UnknownHostException e) {
            logger.error("Host unknown: ",e);
            return false;
        } catch (IOException e) {
            close=true;
            return false;
        }

        return true;

    }

    public void closeChat() {
        try {
            close = true;
            sendMessage(userSet, "Close");
            if (fromServer != null) fromServer.close();
            if (toServer != null) toServer.close();
            if (connect != null) connect.close();
        } catch (Exception e) {

            logger.error("Exception close: ",e);}

    }
    public void closeSocket(){
        try{
            close = true;
            serverDown = true;
            if (fromServer != null) fromServer.close();
            if (toServer != null) toServer.close();
            if (connect != null) connect.close();
        } catch (Exception e) {

        logger.error("Exception close: ",e);}
    }
    
    public void setUserXml(XmlSet userXml) {
        this.userXml = userXml;
    }

    public XmlSet getUserXml() {
        return userXml;
    }

    public synchronized void  getMessage() {
        try {
            //if(close) return;
            if (fromServer == null) throw new IOException();
            BufferedReader is = new BufferedReader(new InputStreamReader(fromServer));
            StringBuffer ans = new StringBuffer();
            while (true) {
                String input = is.readLine();
                ans.append(input);
                if (input == null || input.equals("</XmlMessage>")) {
                    break;
                }
            }
            if(ans.toString().equals("null")){
                throw new SAXException();
            }
            setUserXml(XmlMessage.readXmlFromStream(new ByteArrayInputStream(ans.toString().getBytes())));

        } catch (org.xml.sax.SAXException e1) {
            closeSocket();
            logger.error(" SAXException.Authorization is not passed successfully. ", e1);
            JOptionPane.showMessageDialog(null,"Server is down. Try to reconnect");

        } catch (IOException e) {
            setUserXml(null);
            closeSocket();
            JOptionPane.showMessageDialog(null, "Server is down. Try to reconnect");
        }
    }


    public void sendMessage(XmlSet xml, String message) {
        try {
            if(serverDown) return;
            xml.setPreference(message);

            XmlMessage.writeXMLinStream(xml, toServer);


        } catch (javax.xml.transform.TransformerException e1) {
            logger.error(" TransformerException ", e1);
        }
    }

    public void registration(String login, String password) {
        if (login != null && !login.trim().equals("") && password != null) {
            XmlSet aut = new XmlSet(3);
            java.util.List<String> logPas = new ArrayList<>();
            setMyUser(login);
            logPas.add(login);
            logPas.add(password);
            aut.setList(logPas);
            aut.setKeyDialog(11);
            sendMessage(aut, Preference.Registration.name());
        }
    }

    public void authentication(String login, String password) {
        if (login != null && !login.trim().equals("") && password != null) {
            XmlSet aut = new XmlSet(4);
            java.util.List<String> logPas = new ArrayList<>();
            setMyUser(login);
            logPas.add(login);
            logPas.add(password);
            aut.setList(logPas);
            aut.setKeyDialog(11);
            sendMessage(aut, Preference.Authentication.name());
        }
    }

    public void remove() {
        Object[] options = {"Yes", "No"};
        int n = JOptionPane
                .showOptionDialog(null, SURE,
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            sendMessage(userXml, Preference.Remove.name());
        }
    }

    public void ban(String banUser) {
        List<String> login = new ArrayList<>();
        login.add(banUser);
        userSet.setList(login);
        sendMessage(userSet, Preference.Ban.name());
    }

    public void remove(String removeUser) {
        List<String> login = new ArrayList<>();
        login.add(removeUser);
        userSet.setList(login);
        sendMessage(userSet, Preference.Remove.name());
    }

    public void unBan(String unBanUser) {
        List<String> login = new ArrayList<>();
        login.add(unBanUser);
        userSet.setList(login);
        sendMessage(userSet, Preference.UnBan.name());
    }

    public void sendPrivateMessage(List<String> users, String msg, int keyDialog) {
        if (ban) return;
        userSet.setKeyDialog(keyDialog);
        userSet.setList(users);
        msg = msg.replaceAll("\\n", "<br>");
       List<String> exitUser = new ArrayList<>();
        for(String user:users){
            if(!activeUsers.contains(user)){
                exitUser.add(user);
            }
        }
        exitUser.remove(myUser);
        if(exitUser.size()==0) {
            userSet.setMessage(myUser + ": <br>" + msg);
            sendMessage(userSet, Preference.PrivateMessage.name());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String user:exitUser){
                stringBuilder.append(user + " close chat <br> ");
            }
            userSet.setMessage(stringBuilder.toString() +"<br>"+ myUser + ": <br>" + msg);
            sendMessage(userSet, Preference.PrivateMessage.name());
        }
    }

    public void sendAllMessage(String msg) {
        if (ban) return;
        userSet.setKeyDialog(11);
        msg = msg.replaceAll("\\n", "<br>");
        userSet.setMessage(myUser + ": <br>" + msg);
        sendMessage(userSet, Preference.MessageForAll.name());

    }

    public void createPrChat(List<String> users,String title){
        if (ban) return;
        int keyDialog = 0;
        if (!admin) {
            for (ChatView cv : views) {
                keyDialog = (int) System.currentTimeMillis();
                cv.getUserFrame().createPrivateChat(users, keyDialog,title);
            }
        } else {
            for (ChatView cv : views) {
                keyDialog = (int) System.currentTimeMillis();
                cv.getAdminFrame().createPrivateChat(users, keyDialog,title);
            }
        }
        userSet.setList(users);
        userSet.setKeyDialog(keyDialog);
        userSet.setMessage(title);
        sendMessage(userSet, Preference.PrivateMessage.name());
    }

    public void editUser(String newPassword) {
        java.util.List<String> logPas = new ArrayList<>();
        logPas.add(myUser);
        logPas.add(newPassword);
        userSet.setList(logPas);
        sendMessage(userSet, Preference.Edit.name());
    }

    public String getMyUser() {
        return this.myUser;
    }

    public void setMyUser(String user) {
        this.myUser = user;
    }


    @Override
    public void run() {
        while (true) {
            if (close) return;
            getMessage();
            XmlSet buff = getUserXml();
            if (buff != null) {
                if (!authentication) {
                    if (buff.getPreference().equals(Preference.Registration.name()) &&
                            buff.getMessage().equals(Preference.Successfully.name())) {
                        userSet = getUserXml();
                        for (ChatView cv : views) {
                            cv.closeEnterToChat();
                            cv.createUserView();
                            cv.getUserFrame().setActiveUsers(activeUsers);
                        }
                        authentication = true;
                    }
                    if (buff.getPreference().equals(Preference.Registration.name()) &&
                            buff.getMessage().equals(INCORECT_REGISTRATION)) {
                        JOptionPane.showMessageDialog(null, INCORECT_REGISTRATION);
                    }
                    if (buff.getPreference().equals(Preference.Authentication.name()) && buff.getMessage().equals(Preference.Successfully.name())) {
                        userSet = getUserXml();
                        for (ChatView cv : views) {
                            cv.closeEnterToChat();
                            cv.createUserView();
                            cv.getUserFrame().setActiveUsers(activeUsers);
                        }
                        authentication = true;
                    }
                    if (buff.getPreference().equals(Preference.Authentication.name()) && buff.getMessage().equals(Preference.Ban.name())) {
                        JOptionPane.showMessageDialog(null, BAN);
                        userSet = getUserXml();
                        ban = true;
                        for (ChatView cv : views) {
                            cv.closeEnterToChat();
                            cv.createUserView();
                        }
                        authentication = true;
                    }

                    if (buff.getPreference().equals(Preference.Admin.name())) {
                        admin = true;
                        userSet = getUserXml();
                        sendMessage(getUserXml(), Preference.BanUsers.name());
                        authentication = true;
                    }

                    if (buff.getPreference().equals(Preference.Authentication.name()) && buff.getMessage().equals(ONLINE_USER)) {
                        JOptionPane.showMessageDialog(null, ONLINE_USER);

                    }
                    if (buff.getPreference().equals(Preference.Authentication.name()) && buff.getMessage().equals(EXIST_USER)) {
                        JOptionPane.showMessageDialog(null, EXIST_USER);
                    }
                    if (buff.getPreference().equals(Preference.Authentication.name()) && buff.getMessage().equals(WRONG_COMMAND)) {
                        JOptionPane.showMessageDialog(null, WRONG_COMMAND);
                    }
                }
                if (admin) {
                    if (getUserXml().getPreference().equals(Preference.BanUsers.name()) &&
                            getUserXml().getMessage().equals(Preference.BanUsers.name())) {
                        banUsers = buff.getList();
                        for (ChatView cv : views) {
                            cv.closeEnterToChat();
                            cv.createAdminView();
                            cv.getAdminFrame().setBanUsers(buff.getList());
                            cv.getAdminFrame().setActiveUsers(activeUsers);
                        }
                    }
                    if (buff.getPreference().equals(Preference.Remove.name()) && (buff.getMessage().equals(Preference.Admin.name()))) {
                        JOptionPane.showMessageDialog(null, REMOVE_ADMIN);
                    }
                    if (buff.getPreference().equals(Preference.Ban.name())) {
                        JOptionPane.showMessageDialog(null, BAN_ADMIN);
                        banUsers.add(buff.getList().get(0));
                        for (ChatView cv : views) {
                            cv.getAdminFrame().setBanUsers(banUsers);
                        }
                    }
                    if (buff.getPreference().equals(Preference.UnBan.name())) {
                        JOptionPane.showMessageDialog(null, UNBAN_ADMIN);
                        banUsers.remove(buff.getList().get(0));
                        for (ChatView cv : views) {
                            cv.getAdminFrame().setBanUsers(banUsers);
                        }
                    }
                }
                if (buff.getPreference().equals(Preference.MessageForAll.name())) {
                    try {
                        if (ban) continue;
                        String msg = buff.getMessage().replaceAll("<br>", "\n");
                        if (admin) {
                            for (ChatView cv : views) {
                                cv.getAdminFrame().setAllMessage(msg);
                            }
                        } else {
                            for (ChatView cv : views) {
                                cv.getUserFrame().setAllMessage(msg);
                            }
                        }
                    }catch (NullPointerException e){
                        continue;
                    }
                }
                if (buff.getPreference().equals(Preference.ActiveUsers.name())) {
                    activeUsers = buff.getList();
                    activeUsers.remove(myUser);
                    if (views.get(0).getUserFrame() == null & views.get(0).getAdminFrame() == null) continue;
                    if (admin) {
                        for (ChatView cv : views) {
                            cv.getAdminFrame().setActiveUsers(activeUsers);
                        }
                    } else {
                        for (ChatView cv : views) {
                            cv.getUserFrame().setActiveUsers(activeUsers);
                        }
                    }

                }

                if (buff.getPreference().equals(Preference.PrivateMessage.name())) {
                    if (ban) continue;
                    String msg = buff.getMessage().replaceAll("<br>", "\n");
                    Map<Integer, PrivateChat> privateChatMap;
                    if (admin) {
                        privateChatMap = views.get(0).getAdminFrame().getMap();
                    } else {
                        privateChatMap = views.get(0).getUserFrame().getMap();
                    }
                    if (privateChatMap.containsKey(buff.getKeyDialog())) {
                        if (admin) {
                            for (ChatView cv : views) {
                                cv.getAdminFrame().setPrivateMessage(msg, buff.getKeyDialog());
                            }
                        } else {
                            for (ChatView cv : views) {
                                cv.getUserFrame().setPrivateMessage(msg, buff.getKeyDialog());
                            }
                        }
                    } else {
                        if (buff.getList().contains(myUser)) {
                            Object[] options = {"Yes", "No"};
                            String  question = buff.getList().get(0) +" " +ENTER_TO_PRIVATE;
                            int n = JOptionPane
                                    .showOptionDialog(null, question,
                                            "Confirmation", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE, null, options,
                                            options[0]);
                            if (n == 0) {
                                List<String> privateUser = buff.getList();
                                privateUser.remove(myUser);
                                if (admin) {
                                    for (ChatView cv : views) {
                                        cv.getAdminFrame().createPrivateChat(privateUser, buff.getKeyDialog(),buff.getMessage());
                                    }
                                } else {
                                    for (ChatView cv : views) {
                                        cv.getUserFrame().createPrivateChat(privateUser, buff.getKeyDialog(),buff.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }

                if (buff.getPreference().equals(Preference.Edit.name()) && buff.getMessage().equals(Preference.Successfully.name())) {
                    JOptionPane.showMessageDialog(null, EDIT);
                    myUser = userSet.getList().get(0);
                    for (ChatView cv : views) {
                        cv.getUserFrame().editLogin(myUser);
                    }
                }
                if (buff.getPreference().equals(Preference.Edit.name()) && buff.getMessage().equals(Preference.IncorrectValue.name())) {
                    JOptionPane.showMessageDialog(null, Preference.IncorrectValue.name());
                }

                if (buff.getPreference().equals(Preference.Remove.name()) && (buff.getMessage().equals(Preference.Successfully.name()))) {
                    JOptionPane.showMessageDialog(null, REMOVE);
                    closeChat();
                    System.exit(3);
                }
                if (buff.getPreference().equals(Preference.Remove.name()) && (buff.getMessage().equals(DELETE))) {
                    JOptionPane.showMessageDialog(null, DELETE);
                    closeChat();
                    System.exit(4);
                }

                if (buff.getPreference().equals(Preference.Ban.name()) && (buff.getMessage().equals(Preference.Ban.name()))) {
                    ban = true;
                    JOptionPane.showMessageDialog(null, BAN);
                }
                if (buff.getPreference().equals(Preference.UnBan.name()) && (buff.getMessage().equals(UNBAN))) {
                    ban = false;
                    JOptionPane.showMessageDialog(null, UNBAN);
                }
                if(buff.getPreference().equals(Preference.Stop.name())){
                    closeSocket();
                    JOptionPane.showMessageDialog(null, "Server is down. Try to reconnect");

                }
            }
        }
    }


    public void createView(ViewFactory factory) {
        final ChatView view = factory.createView(this);
        views.add(view);
        if (isConnect) {
            view.createEnterToChat();

        } else {
            close = true;
            final EditConnection editConnection = new EditConnection();
            editConnection.getConnect().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editConnection.getURLField().getText() != null && !editConnection.getURLField().getText().trim().equals("") &&
                            editConnection.getPortField().getText() != null && !editConnection.getPortField().getText().trim().equals("")) {
                        try {
                            PORT = Integer.parseInt(editConnection.getPortField().getText());
                        }catch (NumberFormatException e2){
                            JOptionPane.showMessageDialog(null,"Port must be number!");
                            return;
                        }
                        hostName = editConnection.getURLField().getText();
                        if(connectToServer()){
                            editConnection.closeFrame();
                            close = false;
                            for(ChatView cv:views){
                                cv.createEnterToChat();
                            }
                            start();
                            //view.createEnterToChat();

                        }
                        else {
                            JOptionPane.showMessageDialog(null,"Incorrect, repeat pleas");
                        }
                    }
                }
            });
        }
    }
    public static void main(String[] args) throws IOException, SAXException {
        Controller client = new Controller();
        client.createView(ChatViewSwing.getFactory());
        client.run();
    }
}

