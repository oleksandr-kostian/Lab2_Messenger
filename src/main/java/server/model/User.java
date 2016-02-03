package server.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Class that describes user
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Cloneable {
    private int     id;
    private String  password;
    private String  login;
    private boolean ban;
    private boolean isAdmin;

    /**
     * Constructor that set a unique id when you create user.
     */
    public User() {
        this.id = (int) System.currentTimeMillis();
    }

    protected void setId (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return <code>true</code> if user is in ban,
     *         <code>false</code> if user is not in ban.
     */
    public boolean isBan() {
        return ban;
    }

    /**
     * @param ban <code>true</code> if user is ban,
     *            <code>false</code> if user is not ban.
     */
    public void setBan(boolean ban) {
        this.ban = ban;
    }

    /**
     * Method that return true if user is admin.
     * @return <code>true</code> if user is admin,
     *         <code>false</code> if user is not admin.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public User clone() {
        User result = null;
        try {
            result = (User) super.clone();
            result.setLogin(this.login);
            result.setBan(this.ban);
            result.setPassword(this.password);
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return result;
    }
}
