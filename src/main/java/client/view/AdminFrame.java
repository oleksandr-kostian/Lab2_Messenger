package client.view;

import client.controller.Controller;
import net.miginfocom.swing.MigLayout;
import server.model.XmlSet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by Слава on 24.01.2016.
 */
public class AdminFrame extends UserFrame {
    private Controller controller;
    private XmlSet userSet;
    private JList listBann;
    private List<String> banUsers;
    private int elementBann = -1;
    private DefaultListModel<String> model;
    private Thread adminThead = new Thread(){
        @Override
        public void run() {
            while (true){
                if(isClose()){return;}
                controller.getMessage();
                XmlSet buff = controller.getUserXml();
                if(buff.getPreference().equals("Ban")) {
                    banUsers.add(getActiveUsers().get(getElement()[0]));
                    model.addElement(getActiveUsers().get(getElement()[0]));
                    JOptionPane.showMessageDialog(null, "Ban is successfully");
                }
                if(buff.getPreference().equals("UnBan")) {
                    banUsers.remove(banUsers.get(elementBann));
                    model.remove(elementBann);
                    JOptionPane.showMessageDialog(null, "UnBan is successfully");
                }
            }
        }
    };
    public AdminFrame(Controller controller,List<String> banUsers){
        super(controller,"root",new AdminMenu(),false);
        this.controller = controller;
        userSet = controller.getUserXml();
        this.banUsers = banUsers;
        setBanModel();
        adminThead.start();
    }

    public JPanel setListPanel(){
        JPanel listPanel = new FonPanel();
        listPanel.setLayout(new MigLayout());
        JLabel allView = new JLabel("Active users");
        allView.setForeground(Color.white);
        JLabel bannView = new JLabel("Ban users");
        bannView.setForeground(Color.white);
        setModel();
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
    public void setBanModel(){
        model = new DefaultListModel<>();
        for (String s : banUsers) {
            model.addElement(s);
        }
        listBann.setModel(model);
    }

    public void setMenuListener(){
        super.setMenuListener();
        AdminMenu menu =(AdminMenu) super.getMenu();
        menu.getRemoveUser().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( getElement()!=null && getElement().length==1 ) {
                    int user = getElement()[0];
                    List<String> login = new ArrayList<String>();
                    login.add(getActiveUsers().get(user));
                    userSet.setList(login);
                    controller.sendMessage(userSet, "Remove");

                }
            }
        });

        menu.getBan().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getElement() != null && getElement().length == 1) {
                    List<String> login = new ArrayList<String>();
                    login.add(getActiveUsers().get(getElement()[0]));
                    userSet.setList(login);
                    controller.sendMessage(userSet, "Ban");
                }
            }
        });

        menu.getUnBan().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(elementBann != -1){
                    List<String> login = new ArrayList<String>();
                    login.add(banUsers.get(elementBann));
                    userSet.setList(login);
                    controller.sendMessage(userSet, "UnBan");
                }
            }
        });

    }
}


