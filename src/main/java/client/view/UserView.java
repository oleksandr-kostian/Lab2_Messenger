package client.view;

import java.util.List;
import java.util.Map;

/**
 * Created by Слава on 17.02.2016.
 */
public interface UserView {
    public void createPrivateChat(List<String> privat,int keyDialog);
    public void setPrivateMessage(String msg,int keyDialog);
    public void setAllMessage(String msg);
    public void setActiveUsers(List<String> activeUsers);
    public void editLogin(String login);
    public Map<Integer,PrivateChat> getMap();
    //public PrivateChat getPrivateChat();
}
