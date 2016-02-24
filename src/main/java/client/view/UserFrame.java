package client.view;

import client.controller.Controller;
import client.controller.ControllerActionsClient;
import server.model.XmlSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by Слава on 29.01.2016.
 */
public class UserFrame implements UserView {
    private JTabbedPane tabbedPane;
    private MainPanel allChat;
    private PrivatePanel privateChat;
    private UserMenu menu;
    private java.util.List<String> activeUsers = new ArrayList<>();
    private ControllerActionsClient controller;
    private String login;
    private boolean close;
    private JFrame viewAll;
    private Map<Integer,PrivateChat> keys = new TreeMap<>();
    private List<String> privateList;
    public UserFrame(ControllerActionsClient controller, String login, UserMenu userMenu){
        this.login = login;
        this.controller = controller;
        menu = userMenu;
        createGUI();
        setMenuListener();
    }

    public void setAllChat(MainPanel allChat) {
        this.allChat = allChat;
    }

    public ControllerActionsClient getController() {
        return controller;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public MainPanel getAllChat() {
        return allChat;
    }

    public UserMenu getMenu() {
        return menu;
    }

    public List<String> getActiveUsers() {
        return activeUsers;
    }

    public void createAllChat(List<String> activeUsers){
        allChat = new MainPanel(activeUsers,controller);
        tabbedPane.addTab("All chat",allChat);
    }

    public Map<Integer, PrivateChat> getMap() {
        return keys;
    }


    @Override
    public void createPrivateChat(final List<String> privateList, final int keyDialog) {
        privateChat = new PrivatePanel(privateList,controller);
        privateChat.setKey(keyDialog);
        keys.put(keyDialog,privateChat);
        tabbedPane.addTab("Private chat", privateChat);
    }


    @Override
    public void setPrivateMessage(String msg,int key) {
        PrivateChat privateChat =  getMap().get(key);
        privateChat.setMessage(msg);
    }

    @Override
    public void setAllMessage(String msg) {
        //System.out.println(msg);
        allChat.getMemo().append(msg + "\n");
        allChat.getMemo().append(" \n");
    }
    @Override
    public void setActiveUsers(List<String> activeUsers) {
        this.activeUsers = activeUsers;
        allChat.setActiveUsers(activeUsers);
    }

    @Override
    public void editLogin(String login) {
        viewAll.setTitle(login);
    }

    public boolean isClose() {
        return close;
    }



    public void createGUI(){
        viewAll = new JFrame();
        viewAll.setTitle(login);
        viewAll.setResizable(false);
        viewAll.setContentPane(new FonPanel());

        Font font = new Font("Verdana", Font.PLAIN, 10);
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(font);

        final Container cont = viewAll.getContentPane();

        viewAll.setJMenuBar(menu);
        createAllChat(activeUsers);
        cont.add(tabbedPane);
        viewAll.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                Object[] options = { "Yes", "No" };
                int n = JOptionPane
                        .showOptionDialog(event.getWindow(), "Close chat?",
                                "Confirmation", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    controller.closeChat();
                    System.exit(2);
                }
            }
        });

        viewAll.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        viewAll.pack();
        viewAll.setLocationRelativeTo(null);
        viewAll.setVisible(true);

    }
    public void setMenuListener(){
        menu.getPrivate().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (allChat.getElement() == null) {
                    JOptionPane.showMessageDialog(viewAll, "Choose users");
                    return;
                }
                int select = tabbedPane.getSelectedIndex();
                if (select != 0){
                    JOptionPane.showMessageDialog(viewAll, "For create Private chat use AllChat!");
                    return;
                }
                    privateList = new ArrayList<String>();
                for(int i:allChat.getElement()){
                    privateList.add(activeUsers.get(i));
                }
                controller.sendPrivateMessage(privateList,"Welcome in private chat!",0);
            }
        });
        menu.getEdit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditFrame editFrame = new EditFrame(controller);
            }
        });
        menu.getRemovePrivate().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int select = tabbedPane.getSelectedIndex();
                if (select >= 1) {
                    tabbedPane.removeTabAt(select);
                }
            }
        });
        /*menu.getViewAll().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userSet.setList(activeUsers);
                userSet.setMessage(login + ":  Exited from private chate!!!");
                controller.sendMessage(userSet,"PrivateMessage");
                privateDialog = false;
                menu.getViewAll().setEnabled(false);
            }
        });*/

        menu.getRemove().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.remove();
            }
        });


        /*menu.getExitItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sendMessage(userSet,"Close");
                close = true;
                closeFrame();
                new LoginWindow(controller);
            }
        });*/

    }

    public  void closeFrame(){
        viewAll.setVisible(false);
        viewAll.dispose();
    }



}
