package client.view;

import java.util.List;
import java.util.Map;

/**
 * Created by Слава on 17.02.2016.
 */
public interface UserView {
    /**
     * Method for create private chate
     * @param privateUser users, whom need send message
     * @param keyDialog universal key for private chat
     */
    public void createPrivateChat(List<String> privateUser,int keyDialog,String title);

    /**
     * Method for send message to private chat
     * @param msg message
     * @param keyDialog universal key for private chat
     */
    public void setPrivateMessage(String msg,int keyDialog);

    /**
     * Method for send message to all chat
     * @param msg message
     */
    public void setAllMessage(String msg);

    /**
     * Method for set all active users
     * @param activeUsers all active users
     */
    public void setActiveUsers(List<String> activeUsers);

    /**
     * Method for set  new user login
     * @param login new login
     */
    public void editLogin(String login);

    /**
     * Metod that get map
     * @return map, which contains key - universal key private dialog and value - PrivateChat
     * @see PrivateChat
     * @see Map
     */
    public Map<Integer,PrivateChat> getMap();

}
