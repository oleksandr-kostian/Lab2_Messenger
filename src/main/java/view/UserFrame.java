package view;

//import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Слава on 29.01.2016.
 */
public class UserFrame {
    private int[] element;
    private JList list;
    private UserMenu menu;
    private JPanel listPanel;
    String[] data = new String[]{
        "user1","user2","user3","user4","user5","user6","user7","user8","user9","user10","user11","user12"
    };
    public UserFrame(){
        createGUI();
    }

    public UserMenu setMenu() {
        UserMenu menu = new UserMenu();
        return menu;
    }
    public JPanel setListPanel() {
        final JPanel panel = new FonPanel();
       // panel.setLayout(new MigLayout());
        JLabel listLabel = new JLabel("Список контактов");
        listLabel.setForeground(Color.WHITE);
        list = new JList(data);
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
        viewAll.setTitle("Chat");
        viewAll.setResizable(false);
        viewAll.setContentPane(new FonPanel());
        final Container cont = viewAll.getContentPane();
        JButton send = new JButton("Отправить");
        JTextArea memo = new JTextArea(20,32);
        memo.setLineWrap(true);
        memo.setEnabled(false);
        JTextArea edit = new JTextArea(2,32);
        edit.setLineWrap(true);

        menu = setMenu();

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
                DefaultListModel privateUser = new DefaultListModel();
                for(int i:element){
                privateUser.addElement(data[i]);}
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

        //cont.setLayout(new MigLayout());
        cont.add(jsp1);
        cont.add(listPanel,"wrap");
        cont.add(jsp2);
        cont.add(send);
        viewAll.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewAll.pack();
        viewAll.setLocationRelativeTo(null);
        viewAll.setVisible(true);

    }
}
