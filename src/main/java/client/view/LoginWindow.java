package client.view;

import client.controller.Controller;
import org.xml.sax.SAXException;
import server.model.XmlSet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by Слава on 22.01.2016.
 */
public class LoginWindow extends JFrame {
    private int id = 0;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton ok = new JButton() ;
    private JButton reg = new JButton();
    private Controller controller;

    LoginWindow(Controller controller) {
        super("Enter to chat");
        this.controller = controller;
        createGUI();
    }
    LoginWindow() {
        super("Edit");
        createGUI();
    }

    public JTextField getLoginField() {
        return loginField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public void createGUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setEnterListener(){
        ok.setText("Enter");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*if(loginField.getText()!=null&&loginField.getText().equals("admin")& new String(passwordField.getPassword()).equals("admin")){
                    AdminFrame adminFrame = new AdminFrame(null);
                }else*/
                if (loginField.getText() != null && !loginField.getText().trim().equals("")) {
                    XmlSet aut = new XmlSet(id++);
                    java.util.List<String> logPas = new ArrayList<String>();
                    logPas.add(loginField.getText());
                    logPas.add(new String(passwordField.getPassword()));
                    aut.setList(logPas);
                    aut.setKeyDialog(11);
                    controller.sendMessage(aut, "Authentication");

                    while (true) {
                        controller.getMessage();
                        XmlSet buff = controller.getUserXml();
                        if (buff.getPreference().equals("Authentication") && buff.getMessage().equals("ActiveUsers")) {
                            closeFrame();
                            UserFrame userFrame = new UserFrame(controller, logPas.get(0),new UserMenu());
                            break;
                        }
                        if (buff.getPreference().equals("Authentication") && buff.getMessage().equals("Ban")) {
                            JOptionPane.showMessageDialog(null, "You have ban!!!");
                            break;
                        }

                        if (buff.getPreference().equals("Admin")) {
                            controller.sendMessage(controller.getUserXml(),"BanUsers");
                            while (true){
                                controller.getMessage();
                                if(controller.getUserXml().getPreference().equals("BanUsers")&&
                                        controller.getUserXml().getMessage().equals("BanUsers")){
                                    closeFrame();
                                    AdminFrame adminFrame = new AdminFrame(controller,controller.getUserXml().getList());
                                    break;
                                }
                            }
                            break;
                        }
                        if (buff.getPreference().equals("Authentication") && buff.getMessage().equals("The user is online.")) {
                            JOptionPane.showMessageDialog(null, "The user is online.");
                            break;
                        }
                        if(buff.getPreference().equals("Authentication")){
                            JOptionPane.showMessageDialog(null,
                                    "The client is not authenticated. No token \"authentication\"  word. Please try to connect again.");
                        }
                    }
                }
            }

        });
    }
    public void setRegListener() {
        reg.setText("Registration");
        reg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loginField.getText() != null && !loginField.getText().trim().equals("")) {
                    XmlSet aut = new XmlSet(id++);
                    java.util.List<String> logPas = new ArrayList<String>();
                    logPas.add(loginField.getText());
                    logPas.add(new String(passwordField.getPassword()));
                    aut.setList(logPas);
                    aut.setKeyDialog(11);
                    controller.sendMessage(aut, "Registration");

                    while (true) {
                        controller.getMessage();
                        XmlSet buff = controller.getUserXml();
                        if (buff.getPreference().equals("Registration") && buff.getMessage().equals("ActiveUsers")) {
                            closeFrame();
                            UserFrame userFrame = new UserFrame(controller, logPas.get(0), new UserMenu());
                            return;
                        }
                        if (buff.getPreference().equals("Registration") &&
                                buff.getMessage().equals("IncorrectValue name of user. This user has already been created.")) {
                            JOptionPane.showMessageDialog(null, "IncorrectValue name of user. This user has already been created.");
                            return;
                        }
                    }
                }
            }
        });
    }
    public void closeFrame(){
        this.setVisible(false);
        this.dispose();
    }

    public JButton getOk() {
        return ok;
    }

    public JButton getReg() {
        return reg;
    }

    public static void main(String[] args) throws IOException, SAXException {
        String serverAddress = "localhost";
        Controller client = new Controller(serverAddress);
        client.connectToServer();
        LoginWindow loginWindow = new LoginWindow(client);
    }
}
