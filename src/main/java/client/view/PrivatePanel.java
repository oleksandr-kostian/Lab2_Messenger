package client.view;

import client.controller.ControllerActionsClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Created by Слава on 23.02.2016.
 */
public class PrivatePanel extends MainPanel implements PrivateChat {
    private int key;
    private ControllerActionsClient controller;

    public PrivatePanel(List<String> activeUsers, ControllerActionsClient controller,String title) {
        super(activeUsers, controller);
        this.controller = controller;
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setMessage(String message) {
        getMemo().append(message + "\n");
        getMemo().append(" \n");
    }
    public void setSendListener(){
        getSend().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sendMessage = getMessage();
                if(sendMessage == null) return;
                controller.sendPrivateMessage(getActiveUsers(),sendMessage,getKey());
            }
        });
        getEdit().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    String sendMessage = getMessage();
                    if(sendMessage == null) return;
                    controller.sendPrivateMessage(getActiveUsers(),sendMessage,getKey());
                }

            }

        });

    }
}
