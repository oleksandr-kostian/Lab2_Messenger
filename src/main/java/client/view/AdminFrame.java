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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * Created by Слава on 24.01.2016.
 */
public class AdminFrame extends UserFrame implements AdminView{
    private Controller controller;
    private JList listBann;
    private DefaultListModel<String> model;
    private AdminPanel adminPanel;


    public AdminFrame(Controller controller){
        super(controller,"root",new AdminMenu());
        this.controller = controller;
        adminPanel = (AdminPanel) getAllChat();
    }

    @Override
    public void createAllChat(List<String> activeUsers) {
        setAllChat(new AdminPanel(activeUsers,controller));
        getAllChat().getSend().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message =  getAllChat().getMessage();
                if(message != null){
                    controller.sendAllMessage(message);
                }
            }
        });
        getAllChat().getEdit().addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    String message = getAllChat().getMessage();
                    if (message != null) {
                        controller.sendAllMessage(message);
                    }
                }
            }

        });
        getTabbedPane().addTab("All chat",getAllChat());
    }
    public void setMenuListener(){
        super.setMenuListener();
        AdminMenu menu =(AdminMenu) super.getMenu();
        menu.getRemoveUser().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( getAllChat().getElement()!=null && getAllChat().getElement().length==1 ) {
                    int userIndex = getAllChat().getElement()[0];
                    controller.remove(getActiveUsers().get(userIndex));
                }
            }
        });

        menu.getBan().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getAllChat().getElement() != null && getAllChat().getElement().length == 1) {
                    controller.ban(getActiveUsers().get(getAllChat().getElement()[0]));
                }
            }
        });

        menu.getUnBan().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(adminPanel.getElementBann() != -1){
                  controller.unBan(adminPanel.getBanUsers().get(adminPanel.getElementBann()));
                }
            }
        });

    }

    @Override
    public void setBanUsers(List<String> banUsers) {
        adminPanel.setBanUsers(banUsers);
    }
}


