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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
				onClickAddButton();
			}
		});
	}



	/**
	 * Handler when clicking the "Add button" button
	 */
	public void onClickAddButton() {
		String buttonLabel	= getTextFieldButtonLabel().trim();
		String prefix		= getTextFieldPrefix();
		String postfix		= getTextFieldPostfix();

		if( 	buttonLabel.equals("")
			|| (prefix.equals("") || postfix.equals("")) ) {
			JOptionPane.showMessageDialog(null,"Please enter wrap options for the new button.","No wrap options", JOptionPane.CANCEL_OPTION);
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
