package server.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Class that realize the model
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class Model {
    private static Logger          LOG = Logger.getLogger(Model.class);
    private static UserIO          USERIO;
    private HashMap<Integer, User> list;

    public Model() {
        start();
        addAdmin();
    }

    /**
     * Method for log of all message
     * @param id is key of user
     * @param message  ia a String
     */
    protected static void logMessage(Integer id, String message) {
        try (Writer logMess = new BufferedWriter(new FileWriter("logMessage.txt",true))) {
            String number = String.format("%11d", id);
            logMess.append("id: <").append(number).append(">").append(" - send to all: ")
                    .append("\"").append(message).append("\"").append("\n");

            logMess.flush();
        } catch (FileNotFoundException e) {
            LOG.error("log for messages not found " + e);
        } catch (IOException e) {
            LOG.error("write in log of messages " + e);
        }
    }

    /**
     * method for save all users in a file
     */
    protected void saveHashMapOfUsers(){
        USERIO.writeList(list);
    }

    /**
     * @param login of user
     * @param password of user
     * @return return id of user. If this user did not found then return -1
     */
    public int authorizationUser(String login, String password) {
        for (Map.Entry<Integer, User> user: list.entrySet()) {
            if (user.getValue().getLogin().equals(login)) {
                if (user.getValue().getPassword().equals(password)) {
                    return user.getKey();
                }
            }
        }
        return -1;
    }

    /**
     * @param id of user
     * @return NEW clone of user
     */
    public User getUser(int id) {
        for (Map.Entry<Integer, User> user: list.entrySet()) {
            if (user.getKey().equals(id)) {
                return  user.getValue().clone();
            }
        }
        return null;
    }

    public void addUser(User user) {
        list.put(user.getId(), user);
        saveHashMapOfUsers();
        LOG.info("user add success");
    }

    public void removeUser(User user) {
        list.remove(user.getId());
        saveHashMapOfUsers();
        LOG.info("user remove success");
    }

    public void editUser(User user) {
        list.put(user.getId(), user);
        saveHashMapOfUsers();
        LOG.info("user edit success");
    }

    /**
     * method for start server
     */
    public void start() {
        USERIO  = UserIO.getInstance();

        try {
            list    = USERIO.readList().getHashList();
        } catch (FileNotFoundException e) {
            list = new HashMap<Integer, User>();
        }
        LOG.info("server start");
    }

    /**
     * method for stop server
     */
    public void stop() {
        saveHashMapOfUsers();
        LOG.info("server stop");
        System.exit(0);
    }

    /**
     * Method for create the administrator if it does not exist.
     */
    private void addAdmin() {
        for (Map.Entry<Integer, User> user: list.entrySet()) {
            if (user.getKey().equals(0)) {
                //if admin found then return.
                return;
            }
        }

        // if admin didn't found then create his.
        User admin = new User();
        admin.setId(0);
        admin.setIsAdmin(true);
        admin.setLogin("root");
        admin.setPassword("root");

        addUser(admin);
        LOG.debug("create admin " + admin.getLogin() + admin.getPassword());
    }
}
