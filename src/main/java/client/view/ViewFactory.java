package client.view;

import client.controller.ControllerActionsClient;

/**
 * Created by Слава on 17.02.2016.
 */
public interface ViewFactory {

    ChatView createView(ControllerActionsClient controller);
}
