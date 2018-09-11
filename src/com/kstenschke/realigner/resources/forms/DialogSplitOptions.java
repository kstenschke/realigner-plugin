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
package com.kstenschke.realigner.resources.forms;

import com.kstenschke.realigner.managers.JTextFieldAddUndoManager;
import com.kstenschke.realigner.resources.Icons;

import javax.swing.*;
import java.awt.event.*;

public class DialogSplitOptions extends JDialog {

	private JPanel contentPane;

	private JButton buttonOK;
	private JButton buttonCancel;

	private JRadioButton splitAtDelimiterRadioButton;
	private JRadioButton splitAfterDelimiterRadioButton;
	private JRadioButton splitBeforeDelimiterRadioButton;

	private JTextField textFieldDelimiter;
    private JCheckBox checkboxTrimWhitespace;
    private JLabel labelSplit;

    // Delimiter disposal methods
	private static final int METHOD_DELIMITER_DISPOSAL_AT = 0;
	public static final int METHOD_DELIMITER_DISPOSAL_BEFORE = 1;
	public static final int METHOD_DELIMITER_DISPOSAL_AFTER = 2;

	public boolean clickedOk = false;

	/**
	 * Split options constructor
	 */
	public DialogSplitOptions() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

        initIcons();

		new JTextFieldAddUndoManager(this.textFieldDelimiter);
		buttonOK.addActionListener(e -> onOK());
		buttonCancel.addActionListener(e -> onCancel());

		// Call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// Call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

    private void initIcons() {
        labelSplit.setIcon(Icons.ICON_ARROW_SPLIT);
    }

	/**
	 * Handle click ok event
	 */
	private void onOK() {
		clickedOk = true;
		dispose();
	}

	/**
	 * Handle click cancel event
	 */
	private void onCancel() {
		clickedOk = false;
		dispose();
	}

	/**
	 * Split options main
	 *
	 * @param   args   Arguments
	 */
	public static void main(String[] args) {
		DialogSplitOptions dialog = new DialogSplitOptions();
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}

	/**
	 * Getter for delimiter text
	 *
	 * @return  String
	 */
	public String getDelimiter() {
		return textFieldDelimiter.getText();
	}

	/**
	 * Set delimiter text
	 */
	public void setDelimiter(String delimiter) {
		textFieldDelimiter.setText(delimiter);
	}

	/**
	 * Getter for delimiter disposal method
	 *
	 * @return  Integer
	 */
	public Integer getDelimiterDisposalMethod() {
		if (splitBeforeDelimiterRadioButton.isSelected()) {
			return METHOD_DELIMITER_DISPOSAL_BEFORE;
		}
		if (splitAfterDelimiterRadioButton.isSelected()) {
			return METHOD_DELIMITER_DISPOSAL_AFTER;
		}

		return METHOD_DELIMITER_DISPOSAL_AT;
	}

	/**
	 * Select given split-where (at/after/before delimiter) option radio box
	 *
	 * @param   splitWhere  At/before/after delimiter
	 */
	public void setDelimiterDisposalMethod(Integer splitWhere) {
		switch (splitWhere) {
			case METHOD_DELIMITER_DISPOSAL_BEFORE:
				splitBeforeDelimiterRadioButton.setSelected(true);
				break;

			case METHOD_DELIMITER_DISPOSAL_AFTER:
				splitAfterDelimiterRadioButton.setSelected(true);
				break;

			case METHOD_DELIMITER_DISPOSAL_AT:
			default:
				splitAtDelimiterRadioButton.setSelected(true);
				break;
		}
	}

    /**
     * @param   selected
     */
    public void setCheckboxTrimWhitespaceSelected(boolean selected) {
        checkboxTrimWhitespace.setSelected(selected);
    }

    /**
     * @return  boolean
     */
    public boolean getIsSelectedTrimWhitespace() {
        return checkboxTrimWhitespace.isSelected();
    }
}
