package client.view;

import client.controller.Controller;

/**
 * Created by Слава on 17.02.2016.
 */
public interface ViewFactory {

    ChatView createView(Controller controller);
}
