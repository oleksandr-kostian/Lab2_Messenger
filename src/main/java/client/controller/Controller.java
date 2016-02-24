package client.controller;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.view.*;
import org.xml.sax.SAXException;
import server.controller.Preference;
import server.model.XmlMessage;
import server.model.XmlSet;
import server.view.ServerView;

import javax.swing.*;

/**
 * Client's controller
 * @author Veleri Rechembei, Slavik Miroshnychenko
 * @version %I%, %G%
 */
public class Controller implements Runnable,ControllerActionsClient {
    private String hostName;
    final private int PORT = 1025;
    private Socket connect;
    private String myUser;
    private List<String> activeUsers = new ArrayList<>();
    private InputStream fromServer;
    private OutputStream toServer;
    private XmlSet userXml;
    private XmlSet userSet;
    private boolean ban;
    private boolean admin;
    private boolean close;
    private boolean authentication;
    private List<String> banUsers;
    //private EnterToChat enterToChat;
    private List<ChatView> views = new ArrayList();
    // private static final Logger logger = Logger.getLogger(Controller.class);
    //ClientGUI = gui

    public Controller(String hostName) {
        this.hostName = hostName;
        connectToServer();
    }

    public boolean connectToServer() {
        try {
            connect = new Socket(hostName, PORT);
            System.out.println("Connected: " + connect);
            toServer = connect.getOutputStream();
            fromServer = connect.getInputStream();
        } catch (UnknownHostException uhe) {
            // logger.error("Host unknown: " + uhe.getMessage());
            System.out.println("Host unknown: " + uhe.getMessage());
            return false;
        } catch (IOException e) {
            //  logger.error(e);
            System.out.println("Unexpected exception: " + e.getMessage());
            return false;
        }
        //   new Thread(this).start();

        return true;

    }

    public void closeChat() {
        try {
            close = true;
            sendMessage(userSet, "Close");
            if (fromServer != null) fromServer.close();
        } catch (Exception e) {/*logger.error(e);*/}
        try {
            if (toServer != null) toServer.close();
        } catch (Exception e) {/*logger.error(e);*/}
        try {
            if (connect != null) connect.close();
        } catch (Exception e) {/*logger.error(e);*/}

        // inform the client GUI
        /*if(gui != null)
            gui.connectionFailed();
        */
    }

    public static boolean pingServer(InetAddress serAddress, int port, int timeout) {
        //  logger.info("Ping Server.");
        System.out.println("Ping Server.");
        Socket pingSocket = new Socket();
        Exception exception = null;
        try {
            pingSocket.connect(new InetSocketAddress(serAddress, port), timeout);
            System.out.println("ping...");
        } catch (IOException e) {
            //logger.error("IOException ping server." + e);
            System.out.println("Exception ping server: " + e);

        } finally {
            try {
                pingSocket.close();
            } catch (IOException e) {
                System.out.println("socket.close: " + e);
                // logger.error("IOException socket.close." + e);
            }
        }

        return exception == null;

    }

    public void setUserXml(XmlSet userXml) {
        this.userXml = userXml;
    }

    public XmlSet getUserXml() {
        return userXml;
    }

    public void getMessage() {
        try {
            if(fromServer == null) throw new IOException();
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
            JOptionPane.showMessageDialog(null,"Server is down");
            close = true;
            System.exit(1);
        }
    }


    public void sendMessage(XmlSet xml, String message) {
        try {
            xml.setPreference(message);

            XmlMessage.writeXMLinStream(xml, toServer);


        } catch (javax.xml.transform.TransformerException e1) {
            System.out.println(" TransformerException " + e1);
        }
    }

    public void registration(String login, String password) {
        if (login != null && !login.trim().equals("") && password != null) {
            XmlSet aut = new XmlSet(3);
            java.util.List<String> logPas = new ArrayList<String>();
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
            java.util.List<String> logPas = new ArrayList<String>();
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
                .showOptionDialog(null, "Are you sure?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            sendMessage(userXml, Preference.Remove.name());
        }
    }

    public void ban(String banUser) {
        List<String> login = new ArrayList<String>();
        login.add(banUser);
        userSet.setList(login);
        sendMessage(userSet, Preference.Ban.name());
    }

    public void remove(String removeUser) {
        List<String> login = new ArrayList<String>();
        login.add(removeUser);
        userSet.setList(login);
        sendMessage(userSet, Preference.Remove.name());

    }

    public void unBan(String unBanUser) {
        List<String> login = new ArrayList<String>();
        login.add(unBanUser);
        userSet.setList(login);

        sendMessage(userSet, Preference.UnBan.name());
    }

    public void sendPrivateMessage(List<String> users, String msg, int keyDialog) {
        if (ban) return;
        Map<Integer, PrivateChat> mainPanelMap;
        if (admin) {
            mainPanelMap = views.get(0).getAdminFrame().getMap();
        } else {
            mainPanelMap = views.get(0).getUserFrame().getMap();
        }
        if (keyDialog == 0) {
            if (!admin) {
                for (ChatView cv : views) {
                    keyDialog = (int) System.currentTimeMillis();
                    cv.getUserFrame().createPrivateChat(users, keyDialog);
                }
            } else {
                for (ChatView cv : views) {
                    keyDialog = (int) System.currentTimeMillis();
                    cv.getAdminFrame().createPrivateChat(users, keyDialog);
                }
            }

        }
        //privateDialog = true;
        // user.add(myUser);
        userSet.setKeyDialog(keyDialog);
        userSet.setList(users);
        msg = msg.replaceAll("\\n", "<br>");
        userSet.setMessage(myUser + ": <br>" + msg);
        sendMessage(userSet, Preference.PrivateMessage.name());

    }

    public void sendAllMessage(String msg) {
        if (ban) return;
        userSet.setKeyDialog(11);
       /* msg.replaceAll("\n","s");
        System.out.println(msg);*/
        msg = msg.replaceAll("\\n", "<br>");
        userSet.setMessage(myUser + ": <br>" + msg);
        /*System.out.println("fffffffffffffffffffffffff");
        System.out.println(userSet.getMessage());*/
        sendMessage(userSet, Preference.MessageForAll.name());
    }

    public void editUser(String newLogin, String newPassword) {
        java.util.List<String> logPas = new ArrayList<String>();
        logPas.add(newLogin);
        logPas.add(newPassword);
        userSet.setList(logPas);
        sendMessage(userSet, Preference.Edit.name());
    }


    public void displayToChat(String message) {

    }

    public void viewActiveUser() {

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
            if (close) break;
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
                            buff.getMessage().equals("IncorrectValue name of user. This user has already been created.")) {
                        JOptionPane.showMessageDialog(null, "IncorrectValue name of user. This user has already been created.");
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
                        JOptionPane.showMessageDialog(null, "You have ban!!!");
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

                    if (buff.getPreference().equals(Preference.Authentication.name()) && buff.getMessage().equals("The user is online")) {
                        JOptionPane.showMessageDialog(null, "The user is online");

                    }
                    if (buff.getPreference().equals(Preference.Authentication.name()) && buff.getMessage().equals("User does not exist!")) {
                        JOptionPane.showMessageDialog(null, "User does not exist or you enter wrong password!");
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
                        JOptionPane.showMessageDialog(null, "Removed is successfully");
                    }
                    if (buff.getPreference().equals(Preference.Ban.name())) {
                        JOptionPane.showMessageDialog(null, "Ban is successfully");
                        banUsers.add(buff.getList().get(0));
                        for (ChatView cv : views) {
                            cv.getAdminFrame().setBanUsers(banUsers);
                        }
                    }
                    if (buff.getPreference().equals(Preference.UnBan.name())) {
                        JOptionPane.showMessageDialog(null, "UnBan is successfully");
                        banUsers.remove(buff.getList().get(0));
                        for (ChatView cv : views) {
                            cv.getAdminFrame().setBanUsers(banUsers);
                        }
                    }
                }
                if (buff.getPreference().equals(Preference.MessageForAll.name())) {
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
                }
                if (buff.getPreference().equals(Preference.ActiveUsers.name())) {
                    //if(privateDialog) continue;
                    activeUsers = buff.getList();
                    activeUsers.remove(myUser);
                    for (String s : activeUsers) {
                        System.out.println(s);
                    }
                    if (views.get(0).getUserFrame() == null & views.get(0).getAdminFrame() == null) continue;
                    //if (views.get(0).getUserFrame() == null) continue;
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
                            int n = JOptionPane
                                    .showOptionDialog(null, "Do you want to enter the private chat?",
                                            "Confirmation", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE, null, options,
                                            options[0]);
                            if (n == 0) {

                                //privateDialog = true;
                                DefaultListModel<String> model = new DefaultListModel<>();
                                List<String> privateUser = buff.getList();
                                privateUser.remove(myUser);
                                if (admin) {
                                    for (ChatView cv : views) {
                                        cv.getAdminFrame().createPrivateChat(privateUser, buff.getKeyDialog());
                                    }
                                } else {
                                    for (ChatView cv : views) {
                                        cv.getUserFrame().createPrivateChat(privateUser, buff.getKeyDialog());
                                    }
                                }

                           /* privateUser.remove(login);
                            for (String s : activeUsers) {
                                model.addElement(s);
                            }
                            list.setModel(model);
                            memo.append(buff.getMessage() + "\n");
                            memo.append("\n");
                            menu.getViewAll().setEnabled(true);
                        */
                            }
                        }
                    }
                }

                if (buff.getPreference().equals(Preference.Edit.name()) && buff.getMessage().equals(Preference.Successfully.name())) {
                    //if(edit) continue;
                    JOptionPane.showMessageDialog(null, "Edit is successful.");
                    myUser = userSet.getList().get(0);
                    for (ChatView cv : views) {
                        cv.getUserFrame().editLogin(myUser);
                    }
                    //edit = true;
                }
                if (buff.getPreference().equals(Preference.Edit.name()) && buff.getMessage().equals(Preference.IncorrectValue.name())) {
                    //if(edit) continue;
                    JOptionPane.showMessageDialog(null, Preference.IncorrectValue.name());
                    //edit = true;
                }

                if (buff.getPreference().equals(Preference.Remove.name()) && (buff.getMessage().equals(Preference.Successfully.name()))) {
                    JOptionPane.showMessageDialog(null, "You was remove");
                    closeChat();
                    System.exit(3);
                }
                if (buff.getPreference().equals(Preference.Remove.name()) && (buff.getMessage().equals("Admin deleted you."))) {
                    JOptionPane.showMessageDialog(null, "Admin deleted you!");
                    closeChat();
                    System.exit(4);
                }

                if (buff.getPreference().equals(Preference.Ban.name()) && (buff.getMessage().equals(Preference.Ban.name()))) {
                    ban = true;
                    JOptionPane.showMessageDialog(null, "Admin baned you!");
                }
                if (buff.getPreference().equals(Preference.UnBan.name()) && (buff.getMessage().equals("You was unban"))) {
                    ban = false;
                    JOptionPane.showMessageDialog(null, "Admin unban you!");
                }

            }
        }
    }


    public void createView(ViewFactory factory) {
        ChatView view = factory.createView(this);
        views.add(view);
        view.createEnterToChat();
    }

    public static void main(String[] args) throws IOException, SAXException {
        String serverAddress = "localhost";
        Controller client = new Controller(serverAddress);
        client.createView(ChatViewSwing.getFactory());
        client.run();
    }
}

