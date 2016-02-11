package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Слава on 24.01.2016.
 */
public class AdminMenu extends UserMenu {
    JMenuItem edit;
    JMenuItem remove;
    public AdminMenu() {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        JMenu admin = new JMenu("Admin");
        admin.setFont(font);
        super.add(admin);

        edit = new JMenuItem("Ban");
        edit.setFont(font);
        admin.add(edit);

        remove = new JMenuItem("Remove User");
        remove.setFont(font);
        admin.add(remove);
    }

    public JMenuItem getEditUser() {
        return edit;
    }

    public JMenuItem getRemoveUser() {
        return remove;
    }
}
