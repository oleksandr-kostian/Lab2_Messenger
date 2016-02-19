package client.controller;

import server.model.XmlSet;

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
     * Method for disconnect the connection with server.
     */
    void closeServer();

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
     * @param xml for server
     * @param message is a 'String' message to server.
     */
    void sendMessage(XmlSet xml, String message);

    void displayToChat(String message);
    void viewActiveUser();
    String getMyUser();
    void setMyUser(String user);
}
