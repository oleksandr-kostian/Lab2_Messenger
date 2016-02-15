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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

/**
 * Created by Слава on 29.01.2016.
 */
class UserFrame {
    private int[] element;
    private JList list;
    private UserMenu menu;
    private JPanel listPanel;
    private java.util.List<String> activeUsers;
    private JTextArea memo;
    private Controller controller;
    private XmlSet userSet;
    private String login;
    volatile private boolean privateDialog;
    volatile private boolean close;
    private boolean ban;
    //private boolean edit;
    private JFrame viewAll;
    private List<String> privateList;
    private EditFrame editFrame;
    private Thread  getMess = new Thread() {
        @Override
        public void run() {
            while (true){
                if(close){return;}
                //if(pause){continue;}
                controller.getMessage();
                XmlSet buff = controller.getUserXml();

                if (buff.getPreference().equals("MessageForAll")){
                    if(privateDialog) continue;
                    memo.append(controller.getUserXml().getMessage()+"\n");
                    memo.append("\n");
                }
                if (buff.getPreference().equals("ActiveUsers")){
                    if(privateDialog) continue;
                    DefaultListModel<String> activeUser = new DefaultListModel<>();
                    activeUsers = controller.getUserXml().getList();
                    activeUsers.remove(login);
                    for (String s: activeUsers){
                        activeUser.addElement(s);
                    }
                    list.setModel(activeUser);
                }
                if(buff.getPreference().equals("PrivateMessage")) {
                    if(privateDialog ){
                        memo.append(buff.getMessage()+"\n");
                        memo.append("\n");
                    } else {
                        if (buff.getList().contains(login)) {
                            Object[] options = {"Yes", "No"};
                            int n = JOptionPane
                                    .showOptionDialog(viewAll, "Do you want to enter the private chat?",
                                            "Confirmation", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE, null, options,
                                            options[0]);
                            if (n == 0) {

                                privateDialog = true;
                                DefaultListModel<String> model = new DefaultListModel<>();
                                activeUsers = buff.getList();
                                activeUsers.remove(login);
                                for (String s : activeUsers) {
                                    model.addElement(s);
                                }
                                list.setModel(model);
                                memo.append(buff.getMessage() + "\n");
                                memo.append("\n");
                                menu.getViewAll().setEnabled(true);
                            }
                        }
                    }
                }

                if(buff.getPreference().equals("Edit")&& buff.getMessage().equals("Successfully")){
                    //if(edit) continue;
                    JOptionPane.showMessageDialog(viewAll,"Edit is successful.");
                    login = editFrame.getLoginField().getText();
                    viewAll.setTitle(login);
                    editFrame.dispose();
                    //edit = true;
                }
                if(buff.getPreference().equals("Remove")&&(buff.getMessage().equals("Successfully"))){
                    JOptionPane.showMessageDialog(viewAll,"You was remove");
                    close = true;
                    closeFrame();
                    System.exit(3);
                }
                if(buff.getPreference().equals("Remove")&&(buff.getMessage().equals("Admin deleted you."))){
                    JOptionPane.showMessageDialog(viewAll,"Admin deleted you!");
                    close = true;
                    closeFrame();
                    System.exit(4);
                }

                if(buff.getPreference().equals("Ban")&&(buff.getMessage().equals("Ban"))){
                    ban = true;
                    JOptionPane.showMessageDialog(viewAll,"Admin baned you!");
                }
                if(buff.getPreference().equals("UnBan")&&(buff.getMessage().equals("You was unban"))){
                    ban = false;
                    JOptionPane.showMessageDialog(viewAll,"Admin unban you!");
                }

            }
        }
    };
    private Thread sendMess = new Thread(){
        @Override
        public void run() {
            while (true){
                if(ban){continue;}
                if(close){
                    return;
                }
                if(privateDialog) continue;
                controller.sendMessage(userSet,"ActiveUsers");
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /*new String[]{
        "user1","user2","user3","user4","user5","user6","user7","user8","user9","user10","user11","user12"
    };*/
    public UserFrame(Controller controller,String login,UserMenu userMenu,boolean ban){
        this.ban = ban;
        this.login = login;
        userSet = controller.getUserXml();
        this.controller = controller;
        activeUsers =  userSet.getList();
        activeUsers.remove(login);
        menu = userMenu;
        createGUI();
        setMenuListener();
        getMess.start();
        sendMess.start();
    }

    public UserMenu getMenu() {
        return menu;
    }

    public List<String> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<String> activeUsers) {
        this.activeUsers = activeUsers;
    }

    /*public void setPause(boolean pause) {
        this.pause = pause;
    }*/

    public boolean isClose() {
        return close;
    }

    public void setModel(){
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s: activeUsers){
            model.addElement(s);
        }
        list = new JList(model);
    }

    /*public void setMessageToChat(){
        while (true){
            controller.getMessage();
            if (controller.getUserXml().getPreference().equals("all")){
                memo.setText(controller.getUserXml().getMessage() + "\n");
            }
        }
    }*/

    public int[] getElement() {
        return element;
    }

    public JList setList(){
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        element = list.getSelectedIndices();
                    }
                });
        return list;
    }
    public JPanel setListPanel() {
        final JPanel panel = new FonPanel();
        panel.setLayout(new MigLayout());
        JLabel listLabel = new JLabel("Active users");
        listLabel.setForeground(Color.WHITE);
        setModel();
        list =  setList();
        JScrollPane jsp = new JScrollPane(list);
        jsp.setPreferredSize(new Dimension(120, 300));
        panel.add(listLabel,"wrap");
        panel.add(jsp);
        return panel;
    }

    public void createGUI(){
        viewAll = new JFrame();
        viewAll.setTitle(login);
        viewAll.setResizable(false);
        viewAll.setContentPane(new FonPanel());
        final Container cont = viewAll.getContentPane();

        memo = new JTextArea(20,32);
        memo.setLineWrap(true);
        memo.setWrapStyleWord(true);
       /* memo.setEnabled(false);
        Font font = new Font("Verdana", Font.PLAIN, 11);
        memo.setFont(font);
        memo.setCaretColor(Color.black);
        memo.setSelectionColor(Color.black);*/
        final JTextArea edit = new JTextArea(2,32);
        edit.setWrapStyleWord(true);
        edit.setLineWrap(true);

        JButton send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(ban || edit.getText()==null || edit.getText().trim().equals("")){
                    return;
                }
                if(privateDialog){
                    userSet.setList(activeUsers);
                    userSet.setMessage(login + ":  "+ edit.getText());
                    edit.setText("");
                    controller.sendMessage(userSet,"PrivateMessage");
                    return;
                }else {
                    userSet.setKeyDialog(11);
                    userSet.setMessage(login + ":  "+ edit.getText());
                    edit.setText("");
                    controller.sendMessage(userSet,"MessageForAll");
                }
                //



            }
        });

        JScrollPane jsp1 = new JScrollPane(memo);
        JScrollPane jsp2 = new JScrollPane(edit);

        listPanel = setListPanel();

        viewAll.setJMenuBar(menu);
        cont.setLayout(new MigLayout());
        cont.add(jsp1);
        cont.add(listPanel,"wrap");
        cont.add(jsp2);
        cont.add(send);
        viewAll.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                Object[] options = { "Yes", "No" };
                int n = JOptionPane
                        .showOptionDialog(event.getWindow(), "Close chat?",
                                "Confirmation", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    close = true;
                    controller.sendMessage(userSet,"Close");
                    controller.closeServer();
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
                if (element == null) {
                    JOptionPane.showMessageDialog(viewAll, "Choose users");
                    return;
                }
                privateList = new ArrayList<String>();
                List<String> buff = activeUsers;
                for(int i:element){
                    privateList.add(buff.get(i));}
                activeUsers = privateList;
                userSet.setKeyDialog(12);
                userSet.setList(privateList);
                privateDialog = true;
                userSet.setMessage("private chat");
                controller.sendMessage(userSet,"PrivateMessage");
                DefaultListModel privateUser = new DefaultListModel();
                for (int i : element) {
                    privateUser.addElement(buff.get(i));
                }
                list.setModel(privateUser);
                menu.getViewAll().setEnabled(true);
            }
        });
        menu.getEdit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editFrame = new EditFrame(userSet,controller);
            }
        });

        menu.getViewAll().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userSet.setList(activeUsers);
                userSet.setMessage(login + ":  Exited from private chate!!!");
                controller.sendMessage(userSet,"PrivateMessage");
                privateDialog = false;
                menu.getViewAll().setEnabled(false);
            }
        });

        menu.getRemove().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes", "No"};
                int n = JOptionPane
                        .showOptionDialog(viewAll, "Are you sure?",
                                "Confirmation", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    controller.sendMessage(userSet,"Remove");
                }
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
