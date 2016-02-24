package client.view;

import java.util.List;

/**
 * Created by Слава on 17.02.2016.
 */
public interface AdminView extends UserView {
    /**
     * Method for set users,whom have ban
     * @param banUsers list users
     */
    public void setBanUsers(List<String> banUsers);
}
