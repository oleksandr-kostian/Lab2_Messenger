package view;

import client.controller.Controller;
import org.xml.sax.SAXException;
import server.controller.ControllerServer;
import server.model.XmlSet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

/**
 * Created by Слава on 22.01.2016.
 */
public class LoginWindow extends JFrame {
    private int id = 0;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton ok;
    private Controller controller;

    LoginWindow(Controller controller) {
        super("Вход в систему");
        this.controller = controller;
        createGUI();
    }
    LoginWindow() {
        super("Enter to chat");
        createGUI();
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
        ok =  setButton();
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*if(loginField.getText()!=null&&loginField.getText().equals("admin")& new String(passwordField.getPassword()).equals("admin")){
                    AdminFrame adminFrame = new AdminFrame(null);
                }else*/
                if ( loginField.getText()!=null&&!loginField.getText().trim().equals("")) {
                    XmlSet aut = new XmlSet(id++);
                    java.util.List<String> logPas = new ArrayList<String>();
                    logPas.add(loginField.getText());
                    logPas.add(new String(passwordField.getPassword()));
                    aut.setList(logPas);
                    aut.setKeyDialog(11);
                    controller.sendMessage(aut,"authentication");

                    while (true){
                        controller.getMessage();
                        if (controller.getUserXml().getPreference().equals("authentication") &&
                                controller.getUserXml().getMessage().equals("activeUser") ){
                            UserFrame userFrame = new UserFrame(controller);
                            break;
                        }
                    }


                }

            }
        });
       // JButton cancel = new JButton("Отмена");
        //box3.add(Box.createHorizontalGlue());
        box3.add(ok);
       // box3.add(Box.createHorizontalStrut(12));
        //box3.add(cancel);

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
    /*public void paintComponent(Graphics g){
        Image im = null;
        try {
            im = ImageIO.read(new File("D:\\fon.jpg"));
        } catch (IOException e) {}
        g.drawImage(im, 0, 0, null);
    }*/
    public JButton setButton(){
         JButton enter = new JButton("Вход");
        return enter;
    }

    public static void main(String[] args) throws IOException, SAXException {
        String serverAddress = "localhost";
        Controller client = new Controller(serverAddress);
        client.connectToServer();
        LoginWindow loginWindow = new LoginWindow(client);
    }
}
