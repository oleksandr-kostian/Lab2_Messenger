package server.model;

import java.util.List;

public interface ModelActions {

    /**
     * Method for authorization user on server.
     * @param login of user.
     * @param password of user.
     * @return return id of user. If this user did not found then return -1.
     */
    long authorizationUser(String login, String password);

    /**
     * Method for get list with login of ban users.
     * @return list.
     */
    List<String> getBanList();

    /**
     * Method that set ban for user.
     * @param login of user.
     * @param ban <code>true</code> for ban of user or <code>false</code>.
     * @return <code>true</code> if user ban,
     *         <code>false</code> if login = null.
     */
    boolean setBan(String login, boolean ban);

    /**
     * @param id of user.
     * @return NEW clone of user.
     */
    User getUser(long id);

    /**
     * Method that add user if his login is unique.
     * @param user type of User.
     * @return <code>true</code> if user add,
     *         <code>false</code> if user = null or user exist.
     */
    boolean addUser(User user);

    /**
     * Method that delete user.
     * @param user for delete.
     * @return <code>true</code> if user delete,
     *         <code>false</code> if user = null.
     */
    boolean removeUser(User user);

    /**
     * Method that edit user.
     * @param user type of User.
     * @return <code>true</code> if user edit,
     *         <code>false</code> if user = null or login exists.
     */
    boolean editUser(User user);

    /**
     * method for start server.
     */
    void start();

    /**
     * method for stop server.
     */
    void stop();

    /**
     * Server re-reads its configuration files.
     * @return <code>true</code> if reload success,
     *         <code>false</code> if configuration file has error.
     */
    boolean gracefulReload();
}
