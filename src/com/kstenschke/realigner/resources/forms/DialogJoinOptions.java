/*
 * Copyright 2012-2015 Kay Stenschke
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
package com.kstenschke.realigner.resources.forms;

import com.kstenschke.realigner.managers.JTextFieldAddUndoManager;
import com.kstenschke.realigner.resources.Icons;

import javax.swing.*;
import java.awt.event.*;

public class DialogJoinOptions extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JTextField textFieldGlue;
    private JLabel labelJoin;
    public boolean clickedOk = false;

    /**
     * Constructor
     */
    public DialogJoinOptions() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        initIcons();

        new JTextFieldAddUndoManager(this.textFieldGlue);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initIcons() {
        labelJoin.setIcon(Icons.ICON_ARROW_JOIN);
    }

    private void onOK() {
        clickedOk   = true;
        dispose();
    }

    private void onCancel() {
        clickedOk   = false;
        dispose();
    }

    /**
     * @param   glue
     */
    public void setGlue(String glue) {
        textFieldGlue.setText(glue);
    }

    public static void main(String[] args) {
        DialogJoinOptions dialog = new DialogJoinOptions();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
