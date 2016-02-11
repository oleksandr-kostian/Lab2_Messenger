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
    private XmlSet UserSet;
    private Controller controller;
    private String login;
    public EditFrame(XmlSet UserSet, Controller controller) {
        super(controller);
        this.UserSet = UserSet;
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
                    UserSet.setList(logPas);
                    controller.sendMessage(UserSet, "Edit");
                    setVisible(false);
                    login = getLoginField().getText();
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
