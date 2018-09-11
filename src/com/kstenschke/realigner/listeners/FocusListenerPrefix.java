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

import com.kstenschke.realigner.utils.UtilsTextual;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusListenerPrefix implements FocusListener {

    private final JTextField textFieldPrefix;
    private final JTextField textFieldPostfix;

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
        // When leaving prefix field containing...
        String prefix = textFieldPrefix.getText();
        String postfix= textFieldPostfix.getText();

        // If postfix is empty: detect and insert counterpart of wrap LHS
        if (!postfix.isEmpty()) {
            return;
        }

        postfix = UtilsTextual.getWrapCounterpart(prefix);
        if (null != postfix) {
            textFieldPostfix.setText(postfix);
        }
    }
}
