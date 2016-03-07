package client.view;

/**
 * Created by Слава on 17.02.2016.
 */
public interface ChatView {
    /**
     * Method for create UserView
     * @see UserView
     */
    void createUserView();

    /**
     * Method for create AdminView
     * @see AdminView
     */
    void createAdminView();

    /**
     * Method for create Enter to chat, which let user enter to chat
     */
    void createEnterToChat();

    /**
     * Method for close Enter to chat
     */
    void closeEnterToChat();

    UserView getUserFrame();
    AdminView getAdminFrame();
}
