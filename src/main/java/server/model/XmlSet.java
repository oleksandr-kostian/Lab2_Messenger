package server.model;

import java.util.List;

/**
 * Class that describes a structure of xml message
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlSet {
    private int          idUser;
    private String       message;
    private int          keyMessage;
    private List<String> activeUser;
    private String       elsePreference;

    public XmlSet(int id) {
        this.idUser = id;
    }

    /**
     * @param id of user
     */
    public void setIdUser(int id) {
        this.idUser = id;
    }

    /**
     * @return id of user
     */
    public int getIdUser() {
        return idUser;
    }

    /**
     * @return list of user
     */
    public List<String> getActiveUser() {
        return activeUser;
    }

    /**
     * @param activeUser send list of user
     */
    public void setActiveUser(List<String> activeUser) {
        this.activeUser = activeUser;
    }

    /**
     * @param message send for dialog
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return Sting of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param keyDialog key of dialog
     */
    public void setKeyDialog(int keyDialog) {
        this.keyMessage = keyDialog;
    }

    /**
     *
     * @return key of message
     */
    public int getKeyDialog() {
        return keyMessage;
    }

    /**
     * else preference
     */

    /**
     * @return String of else preference
     */
    public String getElsePreference() {
        return elsePreference;
    }

    /**
     * @param elsePreference is string for send
     */
    public void setElsePreference(String elsePreference) {
        this.elsePreference = elsePreference;
    }
}
