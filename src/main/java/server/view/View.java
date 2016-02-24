package server.view;

import server.controller.Server;

/**
 * Interface for view of server.
 *
 * @author Veleri Rechembei
 * @version %I%, %G%
 */

public interface View  {
    /**
     * Method for display information on GUI.
     * @param display is String message.
     */
    void display(String display);
    /**
     * Method for set server.
     * @param server is server.
     */
    void setServer(Server server);
    /**
     * Method for get server.
     * @return server.
     */
    Server getServer();
    /**
     * Method for get boolean status of server.
     * @return <code>true</code> if server is running.
     *         <code>false</code> if server is stopped.
     */
    boolean isServerStart();
    /**
     * Method for set status of server.
     @param isStart is status for server.
     <code>true</code> if server is running.
     <code>false</code> if server is stopped.
     */
    void setServerStart(boolean isStart);
}
