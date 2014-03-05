/*
 * Copyright 2012-2014 Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kstenschke.realigner;

import com.kstenschke.realigner.resources.forms.DialogWrapOptions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupWrapButton {

    private final JPopupMenu popup;

    /**
     * Constructor
     */
    public PopupWrapButton(final JButton button, final DialogWrapOptions dialog) {
        this.popup = new JPopupMenu();

            // Remove QuickWrap Button
        JMenuItem menuItemSelectedBookmarkAdd    = new JMenuItem(StaticTexts.POPUP_QUICKWRAP_REMOVE);
        menuItemSelectedBookmarkAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsQuickWraps.removeWrapButtonItemFromStore(button.getText());
                dialog.refreshQuickWrapButtons();
            }
        });
        this.popup.add(menuItemSelectedBookmarkAdd);
    }

    /**
     * @return  PopupListener
     */
    public PopupListener getPopupListener() {
        return new PopupListener();
    }

    /**
     * PopupListener
     */
    class PopupListener extends MouseAdapter {
        /**
         * @param   e
         */
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * @param   e
         */
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * @param   e
         */
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
}
