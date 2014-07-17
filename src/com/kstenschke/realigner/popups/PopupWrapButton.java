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
package com.kstenschke.realigner.popups;

import com.kstenschke.realigner.SettingsQuickWraps;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogWrapOptions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.kstenschke.realigner.listeners.PopupListener;

public class PopupWrapButton extends PopupBase {

    private final JPopupMenu popup;

    /**
     * Constructor
     */
    public PopupWrapButton(final JButton button, final DialogWrapOptions dialog) {
        this.popup = new JPopupMenu();

            // Remove QuickWrap Button
        JMenuItem menuItemSelectedBookmarkRemove    = new JMenuItem(StaticTexts.POPUP_QUICKWRAP_REMOVE);
        setJMenuItemIcon(menuItemSelectedBookmarkRemove, "delete.png");
        menuItemSelectedBookmarkRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsQuickWraps.removeWrapButtonItemFromStore(button.getText());
                dialog.refreshQuickWrapButtons();
            }
        });

        this.popup.add(menuItemSelectedBookmarkRemove);
    }

    /**
     * @return  PopupListener
     */
    public PopupListener getPopupListener() {
        return new PopupListener(this.popup);
    }


}
