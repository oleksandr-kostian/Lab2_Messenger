package client.view;

import client.controller.ControllerActionsClient;

/**
 * Interface that describes method for  create view
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
 */
public interface ViewFactory {
    ChatView createView(ControllerActionsClient controller);
}
