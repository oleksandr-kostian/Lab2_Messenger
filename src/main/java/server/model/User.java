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
    private long    id;
    private String  password;
    private String  login;
    private boolean ban;
    private boolean isAdmin;

    /**
     * Constructor that set a unique id when you create user.
     */
    public User() {
        this.id = System.currentTimeMillis();
        /*long g = System.currentTimeMillis(); this.id = (int) g;
        try { Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();})
        //
        int f = (int) g << 32;
        this.id = (int) (f ^ (g >> 32))*/

    }

    public long getId() {
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
        User result;
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

    @Override
    public String toString() {
        /*StringBuilder resalt = new StringBuilder();
        resalt.append("User{").append("id=").append(id).append(", password='").append(password)
                .append("', login='").append(login).append("', ban='").append(ban)
                .append("', isAdmin='").append(isAdmin).append("'}");
        return resalt.toString();*/

        return "User{id=" + id + ", password='" + password +
                "', login='" + login + "', ban='" + ban +
                "', isAdmin='" + isAdmin + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id && (login != null ? login.equals(user.login) : user.login == null);

    }

    @Override
    public int hashCode() {
        int result = (int) id;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }

}
