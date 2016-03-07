package client.view;

import java.util.List;

/**
 * Interface that describes methods for  admin view
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
 */
public interface AdminView extends UserView {
    /**
     * Method for set users,whom have ban
     * @param banUsers list users
     */
    void setBanUsers(List<String> banUsers);
}
