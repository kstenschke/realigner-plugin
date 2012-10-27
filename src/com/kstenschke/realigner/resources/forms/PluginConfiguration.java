package com.kstenschke.realigner.resources.forms;

import com.kstenschke.realigner.Settings;

import javax.swing.*;



public class PluginConfiguration {

	private JPanel rootPanel;
	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;
	private JList list1;
	private JButton addWrapButtonButton;
	private JCheckBox escapeSingleQuotesInsideCheckBox;
	private JCheckBox escapeDoubleQuotesInsideCheckBox;
	private JCheckBox escapeBackslashesInsideWrappedCheckBox;
	private JCheckBox removeBlankWhiteSpaceCheckBox;
	private JButton removeSelectedButtonButton;

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
