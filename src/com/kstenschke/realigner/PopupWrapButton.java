/*
 * Copyright 2012-2018 Kay Stenschke
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

import com.kstenschke.realigner.SettingsQuickWraps;
import com.kstenschke.realigner.listeners.PopupListener;
import com.kstenschke.realigner.resources.Icons;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogWrapOptions;

import javax.swing.*;

public class PopupWrapButton {

    private final JPopupMenu popup;

    /**
     * Constructor
     */
    public PopupWrapButton(final JButton button, final DialogWrapOptions dialog) {
        this.popup = new JPopupMenu();

        // Remove QuickWrap Button
        JMenuItem menuItemSelectedBookmarkRemove    = new JMenuItem(StaticTexts.POPUP_QUICKWRAP_REMOVE);
        menuItemSelectedBookmarkRemove.setIcon(Icons.ICON_DELETE);
        menuItemSelectedBookmarkRemove.addActionListener(e -> {
            SettingsQuickWraps.removeWrapButton(button.getText());
            dialog.refreshQuickWrapButtons();
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
