package view;

//import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Слава on 24.01.2016.
 */
public class AdminFrame extends UserFrame {
    public AdminFrame(){
        super();
       }
    public UserMenu setMenu(){
        UserMenu userMenu = new AdminMenu();
        return userMenu;
    }
    public JPanel setListPanel(){
        JPanel listPanel = new FonPanel();
       // listPanel.setLayout(new MigLayout());
        JLabel allView = new JLabel("all users");
        allView.setForeground(Color.white);
        JLabel bannView = new JLabel("bann users");
        bannView.setForeground(Color.white);
        JList listAll = new JList();
        JScrollPane jsp = new JScrollPane(listAll);
        JList listBann = new JList();
        JScrollPane jsp1 = new JScrollPane(listBann);
        jsp.setPreferredSize(new Dimension(120, 140));
        jsp1.setPreferredSize(new Dimension(120, 140));
        listPanel.add(allView,"wrap");
        listPanel.add(jsp,"wrap");
        listPanel.add(bannView,"wrap");
        listPanel.add(jsp1);
        return listPanel;
    }
}


