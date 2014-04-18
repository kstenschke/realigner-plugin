package com.kstenschke.realigner.listeners;

import com.kstenschke.realigner.UtilsTextual;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusListenerPrefix implements FocusListener {

    private JTextField textFieldPrefix;
    private JTextField textFieldPostfix;

    /**
     * Constructor
     *
     * @param textFieldPrefix
     */
    public FocusListenerPrefix(JTextField textFieldPrefix, JTextField textFieldPostfix) {
        this.textFieldPrefix = textFieldPrefix;
        this.textFieldPostfix = textFieldPostfix;
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        // When leaving prefix field containing an HTML tag: fill with postfix field with resp. pendent
        String prefix = textFieldPrefix.getText();
        if (UtilsTextual.containsHtmlTag(prefix)) {
            textFieldPostfix.setText(UtilsTextual.getClosingTagPendent(prefix));
        }
    }
}
