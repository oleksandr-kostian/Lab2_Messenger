package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Слава on 24.01.2016.
 */
public class AdminMenu extends UserMenu {
    public AdminMenu() {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        JMenu admin = new JMenu("Admin");
        admin.setFont(font);
        super.add(admin);

        JMenuItem edit = new JMenuItem("Ban");
        edit.setFont(font);
        admin.add(edit);

        JMenuItem remove = new JMenuItem("Remove");
        remove.setFont(font);
        admin.add(remove);
    }
}
