package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * *
 * Class that create menu for admin frame
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
 */
public class AdminMenu extends UserMenu {
    private JMenuItem ban;
    private JMenuItem unBan;
    private JMenuItem remove;

    public AdminMenu() {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        JMenu admin = new JMenu("Admin");
        admin.setFont(font);
        super.add(admin);

        ban = new JMenuItem("Ban");
        ban.setFont(font);
        admin.add(ban);

        unBan = new JMenuItem("UnBan");
        unBan.setFont(font);
        admin.add(unBan);

        admin.addSeparator();
        remove = new JMenuItem("Remove User");
        remove.setFont(font);
        admin.add(remove);

    }

    public JMenuItem getBan() {
        return ban;
    }

    public JMenuItem getUnBan() {
        return unBan;
    }

    public JMenuItem getRemoveUser() {
        return remove;
    }

    @Override
    public JMenuItem getRemove() {// admin not remove yourself
        super.getRemove().setEnabled(false);
        return new JMenuItem();
    }
}
