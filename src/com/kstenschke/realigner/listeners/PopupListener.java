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
package com.kstenschke.realigner.listeners;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * PopupListener
 */
public class PopupListener extends MouseAdapter {

    private final JPopupMenu popupMenu;

    /**
     * Constructor
     *
     * @param popupMenu
     */
    public PopupListener(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

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
            this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}