package client.view;

import client.controller.Controller;
import client.controller.ControllerActionsClient;

import java.util.ArrayList;

/**
 * Created by Слава on 17.02.2016.
 */
public class ChatViewSwing implements ChatView {
    private UserFrame userFrame;
    private AdminFrame adminFrame;
    private ControllerActionsClient controller;
    LoginWindow loginWindow;

    public ChatViewSwing(ControllerActionsClient controller) {
        this.controller = controller;
    }

    public static ViewFactory getFactory() {
        return new ViewFactory() {
            public ChatView createView(Controller controller) {
                return new ChatViewSwing(controller);
            }
        };
    }

    @Override
    public void createUserView() {
        userFrame = new UserFrame(controller,controller.getMyUser(),new UserMenu());
    }

    @Override
    public void createAdminView() {
        adminFrame = new AdminFrame(controller);
    }

    @Override
    public void createEnterToChat() {
        loginWindow = new LoginWindow(controller);
    }

    @Override
    public void closeEnterToChat() {
        loginWindow.closeFrame();
    }

    @Override
    public UserView getUserFrame() {
        return userFrame;
    }

    @Override
    public AdminView getAdminFrame() {
        return adminFrame;
    }
}
