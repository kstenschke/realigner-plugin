/*
 * Copyright 2012-2013 Kay Stenschke
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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.kstenschke.realigner.Settings;
import com.kstenschke.realigner.TextualHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WrapOptions extends JDialog {

	private JPanel contentPane;

	private JButton buttonOK;

	private JButton buttonCancel;

	private JTextField textFieldPrefix;

	private JTextField textFieldPostfix;

	private JPanel panelQuickWrapButtons;

	private JPanel panelWrapButtonsContainer;

	private JButton buttonUnwrap;

	public static final int OPERATION_CANCEL = 0;
	public static final int OPERATION_WRAP = 1;
	public static final int OPERATION_UNWRAP = 2;

	public Integer clickedOperation = OPERATION_CANCEL;


	/**
	 * Wrap Options constructor
	 */
	public WrapOptions() {
		clickedOperation = OPERATION_CANCEL;

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		// Init quick wrap buttons from stored wrap button item configs, or hide resp. sub panel
		if (!Settings.areWrapButtonsConfigured()) {
			panelQuickWrapButtons.setVisible(false);
		} else {
			// Add quick wrap buttons
			Object[] allButtonsLabels = Settings.getAllWrapButtonLabels();
			Object[] allButtonPrefixConfigs = Settings.getAllWrapButtonPrefixes();
			Object[] allButtonPostfixConfigs = Settings.getAllWrapButtonPostfixes();

			// Cleanup wrap buttons panel, set layout: grid with a row per quick wrap button
			panelWrapButtonsContainer.removeAll();
			panelWrapButtonsContainer.setLayout(
					  new GridLayoutManager(allButtonsLabels.length, 1, new Insets(0, 0, 0, 0), 0, 0, true, false)
			);

			for (int i = 0; i < allButtonsLabels.length; i++) {
				String buttonLabel = allButtonsLabels[i].toString();
				JButton wrapButton = new javax.swing.JButton(buttonLabel);
				panelWrapButtonsContainer.add(wrapButton, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

				// Add button action
				final String prefix = allButtonPrefixConfigs[i].toString();
				final String postfix = allButtonPostfixConfigs[i].toString();

				wrapButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Set all options from parameters of clicked button
						setTextFieldPrefix(prefix);
						setTextFieldPostfix(postfix);
						onOK();
					}
				});
			}
			panelWrapButtonsContainer.revalidate();
			panelQuickWrapButtons.setVisible(true);
		}

		textFieldPrefix.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {

			}

			@Override
			public void focusLost(FocusEvent e) {
				// When leaving prefix field containing an HTML tag: fill with postfix field with resp. pendent
				String prefix = textFieldPrefix.getText();
				if (TextualHelper.containsHtmlTag(prefix)) {
					textFieldPostfix.setText(TextualHelper.getClosingTagPendent(prefix));
				}
			}
		});

		// Setup button action listeners
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		buttonUnwrap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onUnwrap();
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
		clickedOperation = OPERATION_WRAP;
		dispose();
	}

	/**
	 * Handle click Unwrap event
	 */
	private void onUnwrap() {
		clickedOperation = OPERATION_UNWRAP;
		dispose();
	}

	/**
	 * Handle click cancel event
	 */
	private void onCancel() {
		clickedOperation = OPERATION_CANCEL;
		dispose();
	}

	/**
	 * Get wrap LHS
	 *
	 * @return String
	 */
	public String getTextFieldPrefix() {
		return textFieldPrefix.getText();
	}

	/**
	 * Set wrap LHS
	 */
	public void setTextFieldPrefix(String prefix) {
		textFieldPrefix.setText(prefix);
	}

	/**
	 * Get wrap RHS
	 *
	 * @return String
	 */
	public String getTextFieldPostfix() {
		return textFieldPostfix.getText();
	}

	/**
	 * Set wrap LHS
	 */
	public void setTextFieldPostfix(String postfix) {
		textFieldPostfix.setText(postfix);
	}

	/**
	 * @param   args   Arguments
	 */
	public static void main(String[] args) {
		WrapOptions dialog = new WrapOptions();

		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here          xxx
	}

}
