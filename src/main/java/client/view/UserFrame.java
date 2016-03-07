package client.view;

import client.controller.ControllerActionsClient;


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
    private UserMenu menu;
    private java.util.List<String> activeUsers = new ArrayList<>();
    private ControllerActionsClient controller;
    private String login;
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
    public void createPrivateChat(final List<String> privateList, final int keyDialog,String title) {
        PrivatePanel privateChat = new PrivatePanel(privateList,controller,title);
        privateChat.setKey(keyDialog);
        keys.put(keyDialog,privateChat);
        tabbedPane.addTab(title, privateChat);
        int count = tabbedPane.getTabCount()-1;
        tabbedPane.setSelectedIndex(count);
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
                final SimpleFrame titleChat = new SimpleFrame("Title",new JLabel("Enter title chat pleas"),new JButton("NEXT"));
                titleChat.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        titleChat.close();
                        if(titleChat.getTextField().getText()!=null && !titleChat.getTextField().getText().trim().equals("")) {
                            controller.createPrChat(privateList, titleChat.getTextField().getText());
                        }
                    }
                });

            }
        });
        menu.getEdit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final SimpleFrame editPassword = new SimpleFrame("Edit password",new JLabel("Enter new password"),new JButton("Next"));
                editPassword.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editPassword.close();
                        if(editPassword.getTextField().getText()!=null && !editPassword.getTextField().getText().trim().equals("")) {
                            controller.editUser(editPassword.getTextField().getText());
                        }
                    }
                });
            }
        });
        menu.getRemovePrivate().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int select = tabbedPane.getSelectedIndex();
                if (select >= 1) {
                    PrivatePanel panel = (PrivatePanel) tabbedPane.getSelectedComponent();
                    controller.sendPrivateMessage(panel.getActiveUsers(),"I close chat;",panel.getKey());
                    tabbedPane.removeTabAt(select);
                }
            }
        });

        menu.getRemove().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.remove();
            }
        });
    }




}
