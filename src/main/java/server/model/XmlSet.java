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
    private List<String> activeUser;

    private boolean      openPrivateWindow;
    private int          keyPrivatDialog;
    private String       privateMessage;
    private List<String> privatDialog;

    private String elsePreference;

    public XmlSet(int id) {
        this.idUser = id;
    }

    /**
     * minimum message
     */

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
     * @param mess for send
     */
    public void setGeneralMessage(String mess) {
        this.message = mess;
    }

    /**
     * @return message
     */
    public String getGeneralMessage() {
        return message;
    }

    /**
     * @param act list of active user
     */
    public void setListActiveUser(List act) {
        this.activeUser = act;
    }

    /**
     * @return list of active user
     */
    public List getListActiveUser() {
        return activeUser;
    }


    /**
     * message for private window
     */

    /**
     *
     * @param keyPrivateDialog is int
     * @param privUser list for private window
     */
    public void openPrivateWindow(int keyPrivateDialog, List<String> privUser) {
        this.openPrivateWindow = true;
        this.keyPrivatDialog = keyPrivateDialog;
        this.privatDialog = privUser;
    }

    /**
     * @return true if user needs open the private dialog
     */
    public boolean isOpenPrivateWindow() {
        return openPrivateWindow;
    }

    /**
     * @return list of private window
     */
    public List<String> getListPrivatDialog() {
        return privatDialog;
    }

    public void setPrivatDialog(List<String> privatDialog) {
        this.privatDialog = privatDialog;
    }

    public void setPrivateMessage(int keyPrivateDialog, String privateMessage) {
        this.keyPrivatDialog = keyPrivateDialog;
        this.privateMessage = privateMessage;
    }

    public int getKeyPrivatDialog() {
        return keyPrivatDialog;
    }

    public String getPrivateMessage() {
        return privateMessage;
    }


    /**
     * else preference
     */

    public String getElsePreference() {
        return elsePreference;
    }

    public void setElsePreference(String elsePreference) {
        this.elsePreference = elsePreference;
    }
}
