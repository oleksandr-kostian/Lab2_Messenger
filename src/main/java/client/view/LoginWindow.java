package client.view;


import client.controller.ControllerActionsClient;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Слава on 22.01.2016.
 */
class LoginWindow extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton ok = new JButton() ;
    private JButton reg = new JButton();
    private ControllerActionsClient controller;

    LoginWindow(ControllerActionsClient controller) {
        super("Enter to chat");
        this.controller = controller;
        createGUI();
    }
    public LoginWindow() {
        super("Edit");
        createGUI();
    }
    public void createGUI(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Box box1 = Box.createHorizontalBox();
        final JLabel loginLabel = new JLabel("Login:");
        loginLabel.setForeground(Color.WHITE);
        loginField = new JTextField(15);
        box1.add(loginLabel);
        box1.add(Box.createHorizontalStrut(6));
        box1.add(loginField);

        Box box2 = Box.createHorizontalBox();
        final JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField(15);
        box2.add(passwordLabel);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(passwordField);

        Box box3 = Box.createHorizontalBox();
        setEnterListener();
        setRegListener();
        box3.add(Box.createHorizontalGlue());
        box3.add(ok);
        box3.add(Box.createHorizontalStrut(12));
        box3.add(reg);

        loginLabel.setPreferredSize(passwordLabel.getPreferredSize());

        Box mainBox = Box.createVerticalBox();

        mainBox.setBorder(new EmptyBorder(12,12,12,12));
        mainBox.add(box1);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box2);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(box3);
        JPanel p = new FonPanel();
        p.add(mainBox);
        setContentPane(p);
        pack();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                    controller.closeChat();
                    System.exit(2);
            }
        });
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setEnterListener(){
        ok.setText("Enter");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loginField.getText() != null && !loginField.getText().trim().equals("") && passwordField!=null) {
                    controller.authentication(loginField.getText(),new String(passwordField.getPassword()));
                }
            }

        });

    }
    public void setRegListener() {
        reg.setText("Registration");
        reg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loginField.getText() != null && !loginField.getText().trim().equals("") && passwordField!=null) {
                   controller.registration(loginField.getText(),new String(passwordField.getPassword()));
                }
            }
        });
    }
    public void closeFrame(){
        this.setVisible(false);
        this.dispose();
    }

}
