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

import com.kstenschke.realigner.Settings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;


public class PluginConfiguration {

	public JPanel rootPanel;

	private JTextField textFieldNewLabel;

	private JTextField textFieldNewPrefix;

	private JTextField textFieldNewPostfix;

	private JList listWrapButtons;

	private JButton buttonRemoveSelectedButton;

	private JButton buttonSaveWrapButton;


	/**
	 * Constructor
	 */
	public PluginConfiguration() {

		// When leaving prefix/postfix - auto-generate button label suggestion
		FocusListener focusListenerPrefixPostfix = new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {

			}

			@Override
			public void focusLost(FocusEvent e) {
				String buttonLabelText = textFieldNewPrefix.getText() + " ... " + textFieldNewPostfix.getText();
				textFieldNewLabel.setText(buttonLabelText);
			}
		};

		textFieldNewPrefix.addFocusListener(focusListenerPrefixPostfix);
		textFieldNewPostfix.addFocusListener(focusListenerPrefixPostfix);

		// Add action listeners to save and delete button
		this.buttonSaveWrapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickSaveWrapButton(e);
			}
		});

		buttonRemoveSelectedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickRemoveButton(e);
			}
		});

		// Enable "remove button" only when a button item selected
		listWrapButtons.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Boolean isListSelectionEmpty = listWrapButtons.isSelectionEmpty();
				Boolean isButtonLabelEmpty = textFieldNewLabel.getText().isEmpty();

				buttonRemoveSelectedButton.setEnabled(!isListSelectionEmpty);
				buttonSaveWrapButton.setEnabled(!isListSelectionEmpty || !isButtonLabelEmpty);

				if (!isListSelectionEmpty) {
					// Load selected item config into form
					String selectedItemLabel = listWrapButtons.getSelectedValue().toString().trim();
					Integer selectedLabelStoreIndex = Settings.getLabelIndex(selectedItemLabel);

					setTextFieldButtonLabel(selectedItemLabel);
					setTextFieldPrefix(Settings.getPrefixByIndex(selectedLabelStoreIndex));
					setTextFieldPostfix(Settings.getPostfixByIndex(selectedLabelStoreIndex));
				}
			}
		});

		// Enable/disable "add button" button when button label is entered/empty
		textFieldNewLabel.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				buttonSaveWrapButton.setEnabled(!textFieldNewLabel.getText().isEmpty());
			}
		});

		updateWrapButtonsListItems();
	}

	/**
	 * Initialize wrap buttons list with items from store
	 */
	void updateWrapButtonsListItems() {
		listWrapButtons.setListData(Settings.getAllWrapButtonLabels());
		listWrapButtons.clearSelection();
	}

	/**
	 * @return The selected item's label
	 */
	String getSelectedButtonItemLabel() {
		if (listWrapButtons.isSelectionEmpty()) {
			return "";
		}

		return listWrapButtons.getSelectedValue().toString();
	}

	/**
	 * Handler when clicking the "Add button" button
	 *
	 * @param   e   ActionEvent
	 */
	void onClickSaveWrapButton(ActionEvent e) {
		String buttonLabel = getTextFieldButtonLabel().trim();

		if (buttonLabel.equals("")) {
			JOptionPane.showMessageDialog(null, "Please name the new button with a label.", "No Button Label", JOptionPane.ERROR_MESSAGE);
		} else {
			// Store the button config
			String prefix = getTextFieldPrefix();
			String postfix = getTextFieldPostfix();

			Settings.saveWrapButtonItemToStore(buttonLabel, prefix, postfix);

			updateWrapButtonsListItems();
		}
	}

	/**
	 * Handler when clicking the "Remove selected button" button
	 *
	 * @param   e   ActionEvent
	 */
	void onClickRemoveButton(ActionEvent e) {
		String buttonLabel = getSelectedButtonItemLabel();

		if (!buttonLabel.equals("")) {
			Settings.removeWrapButtonItemFromStore(buttonLabel);
//			Settings.clearStoredWrapButtonItemsConfig();
			updateWrapButtonsListItems();
		}
	}

	/**
	 * Get button label
	 *
	 * @return String
	 */
	String getTextFieldButtonLabel() {
		return textFieldNewLabel.getText();
	}

	/**
	 * Set button label
	 */
	void setTextFieldButtonLabel(String label) {
		textFieldNewLabel.setText(label);
	}

	/**
	 * Get wrap LHS
	 *
	 * @return String
	 */
	String getTextFieldPrefix() {
		return textFieldNewPrefix.getText();
	}

	/**
	 * Set wrap LHS
	 */
	void setTextFieldPrefix(String prefix) {
		textFieldNewPrefix.setText(prefix);
	}

	/**
	 * Get wrap RHS
	 *
	 * @return String
	 */
	String getTextFieldPostfix() {
		return textFieldNewPostfix.getText();
	}

	/**
	 * Set wrap RHS
	 */
	void setTextFieldPostfix(String postfix) {
		textFieldNewPostfix.setText(postfix);
	}

	public JPanel getRootPanel() {
		return rootPanel;
	}

	public boolean isModified() {
		return false;
	}

	public void setData() {

	}

	public void getData() {

	}

}
