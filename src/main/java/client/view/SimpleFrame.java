package client.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Class that create frame for generic work
 * which include need label and button
 * @author  Slavik Miroshnychenko
 * @version %I%, %G%
 */
public class SimpleFrame extends JFrame {
    private JLabel label;
    private JButton button;
    private JTextField title;
    public SimpleFrame(String title, JLabel label, JButton button) throws HeadlessException {
        super(title);
        this.label = label;
        this.button = button;
        createGUI();
    }

    public JButton getButton() {
        return button;
    }

    public JTextField getTextField() {
        return title;
    }

    void  createGUI() {

        Box box1 = Box.createHorizontalBox();
        label.setForeground(Color.WHITE);
        box1.add(label);

        Box box2 = Box.createHorizontalBox();
        title = new JTextField(15);
        box2.add(title);

        Box box3 = Box.createHorizontalBox();
        box3.add(button);

        Box mainBox = Box.createVerticalBox();

        mainBox.setBorder(new EmptyBorder(12,12,12,12));
        mainBox.add(box1);
        mainBox.add(Box.createVerticalStrut(12));
        mainBox.add(box2);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(box3);

        JPanel panel = new FonPanel();
        panel.add(mainBox);
        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void close(){
        setVisible(false);
        dispose();
    }
}
