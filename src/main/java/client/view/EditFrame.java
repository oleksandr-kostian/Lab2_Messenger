package client.view;

import client.controller.ControllerActionsClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Слава on 29.01.2016.
 */
public class EditFrame extends LoginWindow{
    private ControllerActionsClient controller;
    public EditFrame( ControllerActionsClient controller) {
        super(controller);
        createGUI();
        this.controller = controller;
    }
    @Override
    /**
     * Method that set listener on the button for edit user
     */
    public void setEnterListener(){
        JButton edit = super.getOk();
        edit.setText("Edit");
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getLoginField().getText() != null && !getLoginField().getText().trim().equals("")) {
                    controller.editUser(getLoginField().getText(),new String(getPasswordField().getPassword()));
                    closeFrame();
                }
            }
        });
    }
    /**
     * Method that set listener on the button for close form
     */
    @Override
    public void setRegListener(){
        JButton reg = super.getReg();
        reg.setText("Cancel");
        reg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
    }
}
