package view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Слава on 29.01.2016.
 */
public class EditFrame extends LoginWindow{
    public EditFrame() {
        super();
        super.setTitle("Edit");
        createGUI();
    }

    public JButton setButton(){
        JButton enter = new JButton("Save");
        return enter;
    }
}
