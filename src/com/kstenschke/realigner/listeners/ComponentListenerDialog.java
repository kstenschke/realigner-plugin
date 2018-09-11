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
package com.kstenschke.realigner.listeners;

import com.kstenschke.realigner.Preferences;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * ComponentListener for dialogs - storing their position and size
 */
public class ComponentListenerDialog implements ComponentListener {

    private final String idDialog;

    /**
     * Constructor
     */
    public ComponentListenerDialog(String idDialog) {
        this.idDialog = idDialog;
    }

    @Override
    public void componentResized(ComponentEvent e) {
//        Component component = e.getComponent();
//        Preferences.saveDialogSize(this.idDialog, component.getWidth(), component.getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        Component component = e.getComponent();
        Preferences.saveDialogPosition(this.idDialog, component.getX(), component.getY());
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
