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
package com.kstenschke.realigner.managers;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class JTextFieldAddUndoManager {

    private UndoManager undoManager;

    /**
     * Constructor: add UndoManager to given jTextField and its inputMap
     */
    public JTextFieldAddUndoManager(JTextField jTextField) {
        this.undoManager = new UndoManager();

        InputMap inputMap = jTextField.getInputMap();
        putUndoActionToInputMap(inputMap);
        putRedoActionToInputMap(inputMap);

        jTextField.getDocument().addUndoableEditListener(undoManager);
    }

    /**
     * Add redo action to inputMap on CTRL + Z
     *
     * @param inputMap
     */
    private void putRedoActionToInputMap(InputMap inputMap) {
        inputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if( undoManager.canRedo() ) {
                            undoManager.redo();
                        }
                    }
                }
        );
    }

    /**
     * Add undo action to inputMap on CTRL + Z
     *
     * @param inputMap
     */
    private void putUndoActionToInputMap(InputMap inputMap) {
        inputMap.put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if( undoManager.canUndo() ) {
                            undoManager.undo();
                        }
                    }
                }
        );
    }

}
