package client.controller;

/**
 * Interface that describes method of Admin.
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
public interface ControllerActionsAdmin {
    /**
     * Ban sat for user.
     * @param banUser is login of user.
     */
    void ban(String banUser);

    /**
     * Ban cancel for user.
     * @param unBanUser is login of user.
     */
    void unBan(String unBanUser);

}
