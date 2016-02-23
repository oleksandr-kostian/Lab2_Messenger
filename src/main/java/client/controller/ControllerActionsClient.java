package client.controller;

import client.view.ViewFactory;
import server.model.XmlSet;

import java.util.List;

/**
 * Interface that describes method of client controller
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public interface ControllerActionsClient {

    /**
     * Method for connect to server.
     * @return <code>true</code> if user has been connected,
     *         <code>false</code> if user has not been connected.
     */
    boolean connectToServer();

    /**
     * Method that set class of type 'XmlSet' for work with it.
     * @param userXml class of type 'XmlSet'.
     */
    void setUserXml(XmlSet userXml);

    /**
     * Method that get class of type 'XmlSet'.
     * @return class of type 'XmlSet'.
     */
    XmlSet getUserXml();

    /**
     * Method that read message from server to client.
     */
    void getMessage();

    /**
     * Method for send message to server.
     * @param msg is a 'String' message to server.
     */
    void sendAllMessage(String msg);

    /**
     * Method for send private message.
     * @param users     list for dialog.
     * @param msg       of user.
     * @param keyDialog is unique key of dialog.
     */
    void sendPrivateMessage(List<String> users, String msg, int keyDialog);

    /**
     * Method for close chat.
     */
    void closeChat();

    /**
     * Method for registration user on server.
     * @param login    of user
     * @param password of user
     */
    void registration(String login, String password);
    /**
     * Method for authentication user on server.
     * @param login    of user
     * @param password of user
     */
    void authentication(String login, String password);

    /**
     * Method that create view for user.
     * @param factory of view.
     */
    void createView(ViewFactory factory);

    /**
     * Method for remove of user.
     * @param removeUser is login of user.
     */
    void remove(String removeUser);

    /**
     * Method for edit login or password of user.
     * @param newLogin    of user.
     * @param newPassword of user.
     */
    void editUser(String newLogin,String newPassword);
}
