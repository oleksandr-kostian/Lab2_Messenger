package server.model;

import java.util.List;

/**
 * Class that describes a structure of xml message
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public class XmlSet {
    private long         idUser;
    private String       message;
    private int          keyMessage;
    private List<String> list;
    private String       preference;

    public XmlSet(long id) {
        this.idUser = id;
    }

    /**
     * @param id of user
     */
    public void setIdUser(long id) {
        this.idUser = id;
    }

    /**
     * @return id of user
     */
    public long getIdUser() {
        return idUser;
    }

    /**
     * @return list of user
     */
    public List<String> getList() {
        return list;
    }

    /**
     * @param list send list of user
     */
    public void setList(List<String> list) {
        this.list = list;
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
     * @return String of preference
     */
    public String getPreference() {
        return preference;
    }

    /**
     * @param preference is string for send
     */
    public void setPreference(String preference) {
        this.preference = preference;
    }
}
