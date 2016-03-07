package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Class that create menu for user frame
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
 */
public class UserMenu extends  JMenuBar{
    private JMenuItem privat;
    private JMenuItem edit;
    private JMenuItem remove;
    private JMenuItem removePrivate;

    public UserMenu() {
        setMenu();

    }
    public JMenuItem getRemove() {
        return remove;
    }

    public JMenuItem getRemovePrivate() {
        return removePrivate;
    }

    public JMenuItem getEdit() {
        return edit;
    }

    public JMenuItem getPrivate() {
        return privat;
    }


    public void  setMenu() {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        JMenu fileMenu = new JMenu("UserMenu");
        fileMenu.setFont(font);

         privat =  new JMenuItem("Private chat");
        privat.setFont(font);
        fileMenu.add(privat);

         edit = new JMenuItem("Edit password");
        edit.setFont(font);
        fileMenu.add(edit);

        fileMenu.addSeparator();
        remove = new JMenuItem("Remove");
        remove.setFont(font);
        fileMenu.add(remove);

        removePrivate = new JMenuItem("Remove Private Chat");
        removePrivate.setFont(font);
        fileMenu.add(removePrivate);

        this.add(fileMenu);
    }
}
