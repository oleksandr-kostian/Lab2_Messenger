package client.controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 *  Class that create frame for connect to server
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
 */
public class EditConnection extends JFrame {
    private JTextField URLField;
    private JTextField portField;
    private JButton connect = new JButton("Connect");

    public EditConnection() throws HeadlessException {
        super("Try connect");
        createGUI();
    }

    public void createGUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Box box1 = Box.createHorizontalBox();
        final JLabel URLLabel = new JLabel("URL:");
        URLField = new JTextField(15);
        box1.add(URLLabel);
        box1.add(Box.createHorizontalStrut(6));
        box1.add(URLField);

        Box box2 = Box.createHorizontalBox();
        final JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(15);
        box2.add(portLabel);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(portField);

        Box box3 = Box.createHorizontalBox();
        box3.add(connect);

        URLLabel.setPreferredSize(portLabel.getPreferredSize());

        Box mainBox = Box.createVerticalBox();

        mainBox.setBorder(new EmptyBorder(12,12,12,12));
        mainBox.add(box1);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box2);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(box3);
        JPanel p = new JPanel();
        p.add(mainBox);
        setContentPane(p);
        pack();

        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void closeFrame(){
        setVisible(false);
        dispose();
    }

    public JButton getConnect() {
        return connect;
    }


    public JTextField getURLField() {
        return URLField;
    }

    public JTextField getPortField() {
        return portField;
    }
}
