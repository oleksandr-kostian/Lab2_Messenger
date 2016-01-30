package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Слава on 22.01.2016.
 */
public class LoginWindow extends JFrame {

    /* Для того, чтобы впоследствии обращаться к содержимому текстовых полей, рекомендуется сделать их членами класса окна */
    JTextField loginField;
    JPasswordField passwordField;
    JButton ok;

    LoginWindow() {
        super("Вход в систему");
        createGUI();
    }

    public void createGUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);

// Настраиваем первую горизонтальную панель (для ввода логина)
        Box box1 = Box.createHorizontalBox();
        JLabel loginLabel = new JLabel("Логин:");
        loginLabel.setForeground(Color.WHITE);
        loginField = new JTextField(15);
        box1.add(loginLabel);
        box1.add(Box.createHorizontalStrut(6));
        box1.add(loginField);
// Настраиваем вторую горизонтальную панель (для ввода пароля)
        Box box2 = Box.createHorizontalBox();
        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField(15);
        box2.add(passwordLabel);
        box2.add(Box.createHorizontalStrut(6));
        box2.add(passwordField);
// Настраиваем третью горизонтальную панель (с кнопками)
        Box box3 = Box.createHorizontalBox();
        ok =  setButton();
       // JButton cancel = new JButton("Отмена");
        //box3.add(Box.createHorizontalGlue());
        box3.add(ok);
       // box3.add(Box.createHorizontalStrut(12));
        //box3.add(cancel);
// Уточняем размеры компонентов
        loginLabel.setPreferredSize(passwordLabel.getPreferredSize());
// Размещаем три горизонтальные панели на одной вертикальной
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
}
