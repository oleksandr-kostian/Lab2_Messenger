package client.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Слава on 29.01.2016.
 */
class FonPanel extends JPanel {
    /**
     * method for set fon to panel
     * @param g graphics
     */
    public void paintComponent(Graphics g){
        Image im = null;
        try {
            if (new File("classes/fon.jpg").exists()){
                im = ImageIO.read(new File("classes/fon.jpg"));
            } else {
                im = ImageIO.read(new File("src/main/resources/fon.jpg"));
            }
        } catch (IOException e) {}
        g.drawImage(im, 0, 0, null);
    }
}

