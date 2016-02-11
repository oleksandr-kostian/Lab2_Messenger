package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Слава on 22.12.2015.
 */
public class UserMenu extends  JMenuBar{
    private JMenuItem privat;
    private JMenuItem viewAll;
    //private JMenuItem exitItem;
    private JMenuItem edit;
    private JMenuItem remove;
    private JMenuItem change;

    private JMenu fileMenu;

    public UserMenu() {
        setMenu();

    }
    public JMenu getFileMenu() {
        return fileMenu;
    }
    public JMenuItem getRemove() {
        return remove;
    }

    public JMenuItem getChange() {
        return change;
    }

    public JMenuItem getEdit() {
        return edit;
    }

    public JMenuItem getPrivate() {
        return privat;
    }

    public JMenuItem getViewAll() {
        return viewAll;
    }

    /*public JMenuItem getExitItem() {
        return exitItem;
    }*/

    public void  setMenu() {
        Font font = new Font("Verdana", Font.PLAIN, 11);
        fileMenu = new JMenu("UserMenu");
        fileMenu.setFont(font);
       /* JMenu admin = new JMenu("admin");
        fileMenu.add(admin);*/

         privat =  new JMenuItem("Private chat");
        privat.setFont(font);
        fileMenu.add(privat);

         edit = new JMenuItem("Edit account");
        edit.setFont(font);
        fileMenu.add(edit);

        viewAll = new JMenuItem("All chat");
        viewAll.setFont(font);
        fileMenu.add(viewAll);
        viewAll.setEnabled(false);

       /* JMenu task = new JMenu("Task");
        task.setFont(font);
        fileMenu.add(task);

        remove = new JMenuItem("Remove");
        remove.setFont(font);
        task.add(remove);

        change = new JMenuItem("Change");
        change.setFont(font);
        task.add(change);*/
        fileMenu.addSeparator();
        remove = new JMenuItem("Remove");
        remove.setFont(font);
        fileMenu.add(remove);

        //fileMenu.addSeparator();

       /* exitItem = new JMenuItem("Exit");
        exitItem.setFont(font);
        fileMenu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });*/

        this.add(fileMenu);
    }
}
