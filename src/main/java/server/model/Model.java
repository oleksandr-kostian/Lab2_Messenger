package server.model;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import server.controller.Preference;

/**
 * Class that realize the model
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class Model implements ModelActions {
    private static Logger          LOG = Logger.getLogger(Model.class);
    private static UserIO          USERIO;
    private static boolean         statusWork;
    private static boolean         isGui;
    private HashMap<Long, User> list;

    public Model() {
        start();
    }

    /**
     * Method for log of all message.
     * @param id is key of user.
     * @param message  ia a String with text.
     * @param forWhom is a String with direction.
     */
    protected static void logMessage(Long id, String message, String forWhom) {
        try (Writer logMess = new BufferedWriter(new FileWriter("logMessage.txt", true))) {
            String number = String.format("%15d", id);
            logMess.append("id: <").append(number).append("> ")
                    .append(new Date(System.currentTimeMillis()).toString())
                    .append(" - send ").append(forWhom).append(" ")
                    .append("\"").append(message).append("\"")
                    .append("\n");
            logMess.flush();
        } catch (FileNotFoundException e) {
            LOG.error("log for messages not found ", e);
        } catch (IOException e) {
            LOG.error("write in log of messages ", e);
        }
    }

    /**
     * Method for log of all message.
     * @param xmlSet is a instance of message;
     */
    public static void logMessage (XmlSet xmlSet) {
        String forWhom = null;
        if (xmlSet.getPreference().equals(Preference.MessageForAll.name())) {
            forWhom = "to all:";
        }

        if (xmlSet.getPreference().equals(Preference.PrivateMessage.name())) {
            forWhom = "to private, with: ";

            List<String> l = xmlSet.getList();
            for (int i = 0; i < l.size() - 1 ; i++) {
                forWhom += l.get(i) + ", ";
            }
            forWhom += l.get(l.size() - 1) + ".";
        }

        if (forWhom != null) {
            logMessage(xmlSet.getIdUser(), xmlSet.getMessage(), forWhom);
        }
    }

    /**
     * method for save all users in a file
     */
    protected void saveHashMapOfUsers(){
        USERIO.writeList(list);
    }

    /**
     * Method for authorization user on server.
     * @param login of user
     * @param password of user
     * @return return id of user. If this user did not found then return -1
     */
    public long authorizationUser(String login, String password) {
        for (Map.Entry<Long, User> user: list.entrySet()) {
            if (user.getValue().getLogin().equals(login)) {
                if (user.getValue().getPassword().equals(password)) {
                    return user.getKey();
                }
            }
        }
        return -1;
    }

    /**
     * Method for get list with login of ban users.
     * @return list
     */
    public List<String> getBanList() {
      //  List<String> listBan =
               // list.entrySet().stream().filter(user -> user.getValue().isBan())
                        //.map(user -> user.getValue().getLogin()).collect(Collectors.toList());
        List<String> listBan = new ArrayList<>();
        for (Map.Entry<Long, User> user: list.entrySet()) {
            if (user.getValue().isBan()) {
                listBan.add(user.getValue().getLogin());
            }
        }

        return listBan;
    }

    /**
     * Method that set ban for user.
     * @param login of user.
     * @param ban <code>true</code> for ban of user or <code>false</code>.
     * @return <code>true</code> if user ban,
     *         <code>false</code> if login = null.
     */
    public synchronized boolean setBan(String login, boolean ban) {
        if (login == null) {
            return false;
        }

        for (Map.Entry<Long, User> user: list.entrySet()) {
            if (user.getValue().getLogin().equals(login)) {
                user.getValue().setBan(ban);
            }
        }
        saveHashMapOfUsers();
        return true;
    }

    /**
     * @param id of user
     * @return NEW clone of user
     */
    public User getUser(long id) {
        for (Map.Entry<Long, User> user: list.entrySet()) {
            if (user.getKey().equals(id)) {
                return  user.getValue().clone();
            }
        }
        return null;
    }

    /**
     * Method that add user if his login is unique.
     * @param user type of User.
     * @return <code>true</code> if user add,
     *         <code>false</code> if user = null or user exist.
     */
    public synchronized boolean addUser(User user) {
        if (user == null) {
            LOG.debug("user is empty(null)");
            return false;
        }

        for (Map.Entry<Long, User> u : list.entrySet()) {
            if (u.getValue().getLogin().equals(user.getLogin())) {
                return false;
            }
        }

        list.put(user.getId(), user);
        saveHashMapOfUsers();
        LOG.info("user add success: " + user.getLogin());
        LOG.debug("user add: " + user.toString());
        return true;
    }

    /**
     * Method that delete user.
     * @param user for delete.
     * @return <code>true</code> if user delete,
     *         <code>false</code> if user = null.
     */
    public synchronized boolean removeUser(User user) {
        if (user == null) {
            LOG.debug("user is empty(null)");
            return false;
        }

        list.remove(user.getId());
        saveHashMapOfUsers();
        LOG.info("user remove success: " + user.getLogin());
        LOG.debug("user remove: " + user.toString());
        return true;
    }

    /**
     * Method that edit user.
     * @param user type of User.
     * @return <code>true</code> if user edit,
     *         <code>false</code> if user = null or login exists.
     */
    public synchronized boolean editUser(User user) {
        if (user == null) {
            LOG.debug("user is empty(null)");
            return false;
        }

        for (Map.Entry<Long, User> u : list.entrySet()) {
            if (u.getValue().getLogin().equals(user.getLogin()) &&
                    u.getValue().getId() != user.getId()) {
                return false;
            }
        }

        LOG.debug("user for change: " + getUser(user.getId()).toString());

        list.put(user.getId(), user);
        saveHashMapOfUsers();
        LOG.info("user edit success: " + user.getLogin());

        LOG.debug("user change on: " + getUser(user.getId()).toString());
        return true;
    }

    /**
     * method for start server.
     */
    public void start() {
        if (statusWork) {
            return;
        } else {
            statusWork = true;
        }

        gracefulReload();
        USERIO  = UserIO.getInstance();

        try {
            list = USERIO.readList().getHashList();
        } catch (FileNotFoundException e) {
            list = new HashMap<Long, User>();
        }

        addAdmin();
        LOG.info("server start");
    }

    /**
     * method for stop server.
     */
    public void stop() {
        saveHashMapOfUsers();
        statusWork = false;
        LOG.info("server stop");
        //System.exit(0);
    }

    /**
     * Method for create the administrator if it does not exist.
     */
    private void addAdmin() {
        for (Map.Entry<Long, User> user: list.entrySet()) {
            //if (user.getKey().equals(0)) {
            if(user.getValue().isAdmin()) {
                //if admin found then return.
                return;
            }
        }

        // if admin didn't found then create his.
        User admin = new User();
        //admin.setId(0);
        admin.setIsAdmin(true);
        admin.setLogin("root");
        admin.setPassword("root");

        addUser(admin);
        LOG.debug("create admin " + admin.getLogin() + admin.getPassword());
    }

    /**
     * Server re-reads its configuration files.
     * @return <code>true</code> if reload success,
     *         <code>false</code> if configuration file has error.
     */
    public boolean gracefulReload() {
        try {
            ConfigParameters conf = XmlMessageServer.loadProperties();
            isGui = conf.isGUI();
            return true;
        } catch (SAXException e) {
            LOG.error("read properties", e);
        }

        return false;
    }

    /**
     * Method that return parameter of server view.
     * @return <code>true</code> if interface is GUI,
     *         <code>false</code> if interface is not GUI.
     */
    public boolean isGUI() {
        return isGui;
    }
}
