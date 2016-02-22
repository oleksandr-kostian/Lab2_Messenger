package server.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that transform HashMap of users
 * into List for save in file
 *
 * @author Sasha Kostyan
 * @version %I%, %G%
 */
@XmlRootElement(name = "ListUsers")
@XmlAccessorType(XmlAccessType.FIELD)
class ListUser {

    @XmlElement(name = "user")
    private List<User> list = new ArrayList<>();

    /**
     * @param listUser is HashMap of users
     */
    public void setList(HashMap<Long, User> listUser) {
        for (Map.Entry<Long, User> ent: listUser.entrySet()) {
            list.add(ent.getValue());
        }
    }

    /**
     * @return sheet changes
     */
    public List<User> getList() {
        return list;
    }

    /**
     * @return list as HashMap<Integer, User>
     */
    public HashMap<Long, User> getHashList() {
        HashMap<Long, User> hash = new HashMap<>();
        for (User u: list) {
            hash.put(u.getId(), u);
        }
        return hash;
    }
}
