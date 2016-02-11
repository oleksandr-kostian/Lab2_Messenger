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
    Controller controller;
    XmlSet XmlUser;
    JList listBann;
    List<String> banUsers;
    private int elementBann;
    /*Thread adminThead = new Thread(){
        @Override
        public void run() {
            while (true){
                controller.getMessage();
                XmlSet buff = controller.getUserXml();
            }
        }
    };*/
    public AdminFrame(Controller controller,List<String> banUsers){
        super(controller,"root",new AdminMenu());
        this.controller = controller;
        XmlUser = controller.getUserXml();
        this.banUsers = banUsers;
        setBanModel();
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
        final JList listBann = new JList();
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
        DefaultListModel<String> model = new DefaultListModel<>();
        if(banUsers.size() > 0) {
            for (String s : banUsers) {
                model.addElement(s);
            }
            listBann.setModel(model);
        }else return;


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
                    XmlUser.setList(login);
                    controller.sendMessage(XmlUser, "Remove");
                    //adminThead.start();
                }
            }
        });
    }
}


