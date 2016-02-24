package client.view;

/**
 * Created by Слава on 17.02.2016.
 */
public interface ChatView {
    /**
     * Method for create UserView
     * @see UserView
     */
    public void createUserView();

    /**
     * Method for create AdminView
     * @see AdminView
     */
    public void createAdminView();

    /**
     * Method for create Enter to chat, which let user enter to chat
     */
    public void createEnterToChat();

    /**
     * Method for close Enter to chat
     */
    public void closeEnterToChat();

    public UserView getUserFrame();
    public AdminView getAdminFrame();
}
