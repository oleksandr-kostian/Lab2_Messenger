package client.view;

/**
 * Interface that describes methods for  private chat view
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
 */
public interface PrivateChat {
    /**
     * Method for set universal key private chat
     * @param key universal key
     */
    void setKey(int key);

    /**
     * Method that get key
     * @return universal key
     */
    int getKey();

    void setMessage(String message);
    String getMessage();
}
