package server.model;

import java.io.*;
import java.util.HashMap;
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
    }

    /**
     * Method for log of all message
     * @param idDialog is key of dialog
     * @param message  ia a String
     */
    protected static void logMessage(Integer idDialog, String message) {
        try (Writer logMess = new BufferedWriter(new FileWriter("logMessage.txt",true))) {
            logMess.append(idDialog.toString()).append(": ").append(message).append("\n");
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
        list    = USERIO.readList().getHashList();
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
}
