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

package com.kstenschke.realigner.resources.forms;

import com.kstenschke.realigner.Settings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;


public class PluginConfiguration {

	private JPanel rootPanel;
	private JTextField textFieldNewLabel;
	private JTextField textFieldNewPrefix;
	private JTextField textFieldNewPostfix;
	private JList listWrapButtons;
	private JButton buttonRemoveSelectedButton;
	private JButton buttonAddWrapButton;
	private JCheckBox checkBoxNewEscapeSingleQuotesInsideWrapped;
	private JCheckBox checkBoxNewEscapeDoubleQuotesInsideWrapped;
	private JCheckBox checkBoxNewEscapeBackslashesInsideWrapped;
	private JCheckBox checkBoxNewRemoveBlankWhiteSpace;

	/**
	 * Constructor
	 */
	public PluginConfiguration() {
		buttonAddWrapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickAddButton(e);
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
				buttonRemoveSelectedButton.setEnabled( !listWrapButtons.isSelectionEmpty() );
			}
		});

			// Enable "add button" button only when button label is given
		textFieldNewLabel.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent e) {}

			@Override
			public void focusLost(FocusEvent e) {
				buttonAddWrapButton.setEnabled( !textFieldNewLabel.getText().isEmpty() );
			}
		});

		 updateWrapButtonsListItems();
	}


	/**
	 *	Initialize wrap buttons list with items from store
	 */
	public void updateWrapButtonsListItems() {
		listWrapButtons.setListData( Settings.getWrapButtonItemsLabels() );
		listWrapButtons.clearSelection();
	}



	/**
	 * @return	The selected item's label
	 */
	public String getSelectedButtonItemLabel() {
		if( listWrapButtons.isSelectionEmpty() ) {
			return "";
		}

		return listWrapButtons.getSelectedValue().toString();
	}



	/**
	 * Handler when clicking the "Add button" button
	 *
	 * @param	e	ActionEvent
	 */
	public void onClickAddButton(ActionEvent e) {
		String buttonLabel	= getTextFieldButtonLabel().trim();

		if( 	buttonLabel.equals("") ) {
			JOptionPane.showMessageDialog(null,"Please name the new button with a label.","No Button Label", JOptionPane.CANCEL_OPTION);
		} else {
				// Store the new button
			String prefix		= getTextFieldPrefix();
			String postfix		= getTextFieldPostfix();
			Boolean escapeSingeQuotes	= isSelectedEscapeSingleQuotes();
			Boolean escapeDoubleQuotes	= isSelectedEscapeDoubleQuotes();
			Boolean escapeBackslashes	= isSelectedEscapeBackslashes();
			Boolean removeBlankLines	= isSelectedRemoveBlankLines();

			Settings.addWrapButtonItemToStore(buttonLabel, prefix, postfix, escapeSingeQuotes, escapeDoubleQuotes, escapeBackslashes, removeBlankLines);

			updateWrapButtonsListItems();
		}
	}



	/**
	 * Handler when clicking the "Remove selected button" button
	 *
	 * @param	e	ActionEvent
	 */
	public void onClickRemoveButton(ActionEvent e) {
		String buttonLabel	= getSelectedButtonItemLabel();

		if( !buttonLabel.equals("") ) {
			Settings.removeWrapButtonItemFromStore(buttonLabel);
//			Settings.clearStoredWrapButtonItemsConfig();
			updateWrapButtonsListItems();
		}
	}



	/**
	 * Get button label
	 *
	 * @return	String
	 */
	public String getTextFieldButtonLabel() {
		return textFieldNewLabel.getText();
	}

//	/**
//	 * Set button label
//	 */
//	public void setTextFieldButtonLabel(String label) {
//		textFieldNewLabel.setText(label);
//	}



	/**
	 * Get wrap LHS
	 *
	 * @return	String
	 */
	public String getTextFieldPrefix() {
		return textFieldNewPrefix.getText();
	}

//	/**
//	 * Set wrap LHS
//	 */
//	public void setTextFieldPrefix(String prefix) {
//		textFieldNewPrefix.setText(prefix);
//	}



	/**
	 * Get wrap RHS
	 *
	 * @return	String
	 */
	public String getTextFieldPostfix() {
		return textFieldNewPostfix.getText();
	}



//	/**
//	 * Set wrap LHS
//	 */
//	public void setTextFieldPostfix(String postfix) {
//		textFieldNewPostfix.setText(postfix);
//	}



	/**
	 * Check whether wrapped single quotes are selected to be escaped
	 *
	 * @return	Boolean.
	 */
	public Boolean isSelectedEscapeSingleQuotes() {
		return checkBoxNewEscapeSingleQuotesInsideWrapped.isSelected();
	}

//	public void setSelectedEscapeSingleQuotes(Boolean setSelected) {
//		escapeSingleQuotesCheckBox.setSelected(setSelected);
//	}




	/**
	 * Check whether wrapped double quotes are selected to be escaped
	 *
	 * @return	Boolean.
	 */
	public Boolean isSelectedEscapeDoubleQuotes() {
		return checkBoxNewEscapeDoubleQuotesInsideWrapped.isSelected();
	}

//	public void setSelectedEscapeDoubleQuotes(Boolean setSelected) {
//		escapeDoubleQuotesCheckBox.setSelected(setSelected);
//	}



	/**
	 * Check whether wrapped double quotes are selected to be escaped
	 *
	 * @return	Boolean.
	 */
	public Boolean isSelectedEscapeBackslashes() {
		return checkBoxNewEscapeBackslashesInsideWrapped.isSelected();
	}

//	public void setSelectedEscapeBackslashes(Boolean setSelected) {
//		checkBoxNewEscapeBackslashesInsideWrapped.setSelected(setSelected);
//	}



	public Boolean isSelectedRemoveBlankLines() {
		return checkBoxNewRemoveBlankWhiteSpace.isSelected();
	}

//	public void setSelectedRemoveBlankLines(Boolean setSelected) {
//		checkBoxNewRemoveBlankWhiteSpace.setSelected(setSelected);
//	}



	public JPanel getRootPanel() {
		return rootPanel;
	}

	public boolean isModified(Settings data) {
		return false;
	}

	public void setData(Settings data) {

	}

	public void getData(Settings data) {

	}
}
