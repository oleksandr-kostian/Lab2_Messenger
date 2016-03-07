package client.view;

/**
 * Interface that describes methods for chat view
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
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

    /**
     * Method that get to created user view
     * @return user view
     */
    UserView getUserFrame();

    /**
     * Method that get to created admin view
     * @return admin view
     */
    AdminView getAdminFrame();
}
