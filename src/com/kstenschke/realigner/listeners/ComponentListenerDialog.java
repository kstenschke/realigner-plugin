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
