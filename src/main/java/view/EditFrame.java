package view;

//import net.miginfocom.swing.MigLayout;

import javax.swing.*;

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
        return  new JButton("Save");
    }
}
