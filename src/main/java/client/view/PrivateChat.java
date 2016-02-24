package client.view;

/**
 * Created by Слава on 22.02.2016.
 */
public interface PrivateChat {
    /**
     * Method for set universal key private chat
     * @param key universal key
     */
    public void setKey(int key);

    /**
     * Method that get key
     * @return universal key
     */
    public int getKey();

    public void setMessage(String message);
    public String getMessage();
}
