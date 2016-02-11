package client.view;

//import net.miginfocom.swing.MigLayout;

import client.controller.Controller;
import server.model.XmlSet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Слава on 29.01.2016.
 */
public class EditFrame extends LoginWindow{
    XmlSet XMLUser;
    Controller controller;
    public EditFrame(XmlSet xmlSet, Controller controller) {
        super(controller);
        XMLUser = xmlSet;
        createGUI();
        this.controller = controller;
    }
  @Override
    public void setEnterListener(){
        JButton edit = super.getOk();
        edit.setText("Edit");
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getLoginField().getText() != null && !getLoginField().getText().trim().equals("")) {
                    java.util.List<String> logPas = new ArrayList<String>();
                    logPas.add(getLoginField().getText());
                    logPas.add(new String(getPasswordField().getPassword()));
                    XMLUser.setList(logPas);
                    controller.sendMessage(XMLUser, "Edit");
                    setVisible(false);
                    dispose();
                    /*while(true){
                        controller.getMessage();
                        if(controller.getUserXml().getPreference().equals("Edit")&&
                                controller.getUserXml().getMessage().equals("Successfully")){
                            JOptionPane.showMessageDialog(null,"Edit is successful.");
                            return;
                        }

                    }*/

                }
            }
        });
    }

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
