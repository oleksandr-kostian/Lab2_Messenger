package client.view;

import client.controller.Controller;
import client.controller.ControllerActionsClient;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Слава on 22.02.2016.
 */
public class AdminPanel extends MainPanel {
    private JList listBann;
    private List<String> banUsers = new ArrayList<>();
    private int elementBann = -1;

    public AdminPanel(List<String> activeUsers, ControllerActionsClient controller) {
        super(activeUsers,controller);
    }
    public JPanel setListPanel(){
        JPanel listPanel = new FonPanel();
        listPanel.setLayout(new MigLayout());
        JLabel allView = new JLabel("Active users");
        allView.setForeground(Color.white);
        JLabel bannView = new JLabel("Ban users");
        bannView.setForeground(Color.white);
        JList list = super.setList();
        JScrollPane jsp = new JScrollPane(list);
        listBann = new JList();
        listBann.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listBann.addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        elementBann = listBann.getSelectedIndex();
                    }
                });

        JScrollPane jsp1 = new JScrollPane(listBann);
        jsp.setPreferredSize(new Dimension(120, 140));
        jsp1.setPreferredSize(new Dimension(120, 140));
        listPanel.add(allView,"wrap");
        listPanel.add(jsp,"wrap");
        listPanel.add(bannView,"wrap");
        listPanel.add(jsp1);
        return listPanel;
    }

    public List<String> getBanUsers() {
        return banUsers;
    }

    public void  setBanUsers(List<String> banUsers){
        this.banUsers = banUsers;
        DefaultListModel<String> banUser = new DefaultListModel<>();
        for (String s: banUsers){
            banUser.addElement(s);
        }
        listBann.setModel(banUser);
    }

    public int getElementBann() {
        return elementBann;
    }
}
