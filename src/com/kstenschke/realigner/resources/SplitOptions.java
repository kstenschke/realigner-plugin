/*
 * Copyright 2012 Kay Stenschke
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

package com.kstenschke.realigner.resources;

import javax.swing.*;
import java.awt.event.*;

public class SplitOptions extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JRadioButton splitAtDelimiterAndRadioButton;
	private JRadioButton splitAfterDelimiterRadioButton;
	private JRadioButton splitBeforeDelimiterRadioButton;
	private JTextField textFieldDelimiter;

	// Delimiter disposal methods
	public static final int METHOD_DELIMITERDISPOSAL_AT = 0;
	public static final int METHOD_DELIMITERDISPOSAL_BEFORE = 1;
	public static final int METHOD_DELIMITERDISPOSAL_AFTER = 2;

	public Boolean clickedOk   = false;



	/**
	 * Split options constructor
	 */
	public SplitOptions() {
		clickedOk   = false;

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

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

// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}



	/**
	 * Handle click ok event
	 */
	private void onOK() {
		clickedOk   = true;
		dispose();
	}



	/**
	 * Handle click cancel event
	 */
	private void onCancel() {
		clickedOk   = false;
		dispose();
	}



	/**
	 * Split options main
	 *
	 * @param	args
	 */
	public static void main(String[] args) {
		SplitOptions dialog = new SplitOptions();
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
	 * Getter for delimiter disposal method
	 *
	 * @return  Integer
	 */
	public Integer getDelimiterDisposalMethod() {
		if( splitBeforeDelimiterRadioButton.isSelected() ) {
			return METHOD_DELIMITERDISPOSAL_BEFORE;
		}

		if( splitAfterDelimiterRadioButton.isSelected() ) {
			return METHOD_DELIMITERDISPOSAL_AFTER;
		}

		return METHOD_DELIMITERDISPOSAL_AT;
	}

}
