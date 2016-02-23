package client.view;

/**
 * Created by Слава on 17.02.2016.
 */
public interface ChatView {
    public void createUserView();
    public void createAdminView();
    public void createEnterToChat();
    public void closeEnterToChat();
    public UserView getUserFrame();
    public AdminView getAdminFrame();
}
