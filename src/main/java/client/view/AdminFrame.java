package client.view;

import client.controller.ControllerActionsClient;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by Слава on 24.01.2016.
 */
public class AdminFrame extends UserFrame implements AdminView{
    private ControllerActionsClient controller;
    private AdminPanel adminPanel;


    public AdminFrame(ControllerActionsClient controller){
        super(controller,"root",new AdminMenu());
        this.controller = controller;
    }

    @Override
    public void createAllChat(List<String> activeUsers) {

        adminPanel =new AdminPanel(activeUsers,getController());
        setAllChat(adminPanel);
        getTabbedPane().addTab("All chat",adminPanel);
    }
    public void setMenuListener(){
        super.setMenuListener();
        AdminMenu menu =(AdminMenu) super.getMenu();
        menu.getRemoveUser().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int select = getTabbedPane().getSelectedIndex();
                if (select != 0){
                    JOptionPane.showMessageDialog(null, "For remove use AllChat!");
                    return;
                }
                if( getAllChat().getElement()!=null && getAllChat().getElement().length==1 ) {
                    controller.remove(getActiveUsers().get(getAllChat().getElement()[0]));
                }
            }
        });

        menu.getBan().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int select = getTabbedPane().getSelectedIndex();
                if (select != 0){
                    JOptionPane.showMessageDialog(null, "For ban use AllChat!");
                    return;
                }
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


