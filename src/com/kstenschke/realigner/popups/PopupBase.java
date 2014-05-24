package com.kstenschke.realigner.popups;

import com.kstenschke.realigner.Preferences;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

class PopupBase {

    /**
     * @param   jMenuItem
     * @param   pathImage
     */
    void setJMenuItemIcon(JMenuItem jMenuItem, @Nullable String pathImage) {
        if( pathImage == null ) {
            pathImage = "resources/images/blank16x16.png";
        }

        try {
            Class baseClass= Preferences.class;
            Image image    = ImageIO.read(baseClass.getResource(pathImage));
            ImageIcon icon = new ImageIcon(image);
            jMenuItem.setIcon(icon);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

}
