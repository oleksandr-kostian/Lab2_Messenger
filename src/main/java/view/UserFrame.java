package view;

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
    private java.util.List<String> data ;
    private JTextArea memo;
    private Controller controller;
    private XmlSet userSet;
    private String login;
    private Thread  getMess = new Thread() {
        @Override
        public void run() {
            while (true){
                controller.getMessage();
                if (controller.getUserXml().getPreference().equals("message to all")){
                    memo.append(controller.getUserXml().getMessage()+"\n");
                    memo.append("\n");
                }
                if (controller.getUserXml().getPreference().equals("activeUser")){
                    DefaultListModel<String> activeUser = new DefaultListModel<>();
                    data = controller.getUserXml().getList();
                    data.remove(login);
                    for (String s: data){
                        activeUser.addElement(s);
                    }
                    list.setModel(activeUser);
                }
            }
        }
    };
    private Thread sendMess = new Thread(){
        @Override
        public void run() {
            while (true){
                controller.sendMessage(userSet,"activeUser");
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /*new String[]{
        "user1","user2","user3","user4","user5","user6","user7","user8","user9","user10","user11","user12"
    };*/
    public UserFrame(Controller controller,String login){
        this.login = login;
        userSet = controller.getUserXml();
        this.controller = controller;
        data =  userSet.getList();
        data.remove(login);
        createGUI();
        getMess.start();
        sendMess.start();
    }

    public UserMenu setMenu() {
        UserMenu menu = new UserMenu();
        return menu;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setList(){
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s:data){
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

    public JPanel setListPanel() {
        final JPanel panel = new FonPanel();
        panel.setLayout(new MigLayout());
        JLabel listLabel = new JLabel("Список контактов");
        listLabel.setForeground(Color.WHITE);
        setList();
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        element = list.getSelectedIndices();
                    }
                });
        JScrollPane jsp = new JScrollPane(list);
        jsp.setPreferredSize(new Dimension(120, 300));
        panel.add(listLabel,"wrap");
        panel.add(jsp);
        return panel;
    }

    public void createGUI(){
        final JFrame viewAll = new JFrame();
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

        menu = setMenu();

        JButton send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userSet.setKeyDialog(11);
                if(edit.getText()==null|edit.getText().trim().equals("")){
                    return;
                }
                userSet.setMessage(login + ":  "+ edit.getText());
                edit.setText("");
                controller.sendMessage(userSet,"all");
            }
        });

        JScrollPane jsp1 = new JScrollPane(memo);
        JScrollPane jsp2 = new JScrollPane(edit);


        listPanel = setListPanel();


        viewAll.setJMenuBar(menu);
        JMenuItem priv =  menu.getPrivate();
        priv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //viewAll.setVisible(false);
                //viewAll.dispose();
                //createGUI();
                if (element == null) {
                    JOptionPane.showMessageDialog(viewAll, "Choose users");
                    return;
                }
                //viewAll.setTitle("Private message");
                List<String> privateList = new ArrayList<String>();
                List<String> buff =  data;
                for(int i:element){
                    privateList.add(buff.get(i));}
                for(String s:privateList){
                    System.out.println(s);
                }
                userSet.setList(privateList);
                controller.sendMessage(userSet,"private");
                /*while (true) {
                    controller.getMessage();
                    if (controller.getUserXml().getPreference().equals("private")) {
                  */
                DefaultListModel privateUser = new DefaultListModel();
                for (int i : element) {
                    privateUser.addElement(buff.get(i));
                }
                list.setModel(privateUser);



                //UserFrame fr = new UserFrame();
            }
        });
        /*
       JMenuItem view = menu.getViewAll();
        view.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel allUser = new DefaultListModel();
                for (String s:data){
                    allUser.addElement(s);
                }
                list.setModel(allUser);

            }
        });*/

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
                    controller.sendMessage(userSet,"close");
                    controller.closeServer();
                    System.exit(2);
                }
            }
        });

        viewAll.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewAll.pack();
        viewAll.setLocationRelativeTo(null);
        viewAll.setVisible(true);

    }


}
