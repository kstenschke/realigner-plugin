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
package com.kstenschke.realigner.resources.forms;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.kstenschke.realigner.popups.PopupWrapButton;
import com.kstenschke.realigner.Preferences;
import com.kstenschke.realigner.SettingsQuickWraps;
import com.kstenschke.realigner.listeners.FocusListenerPrefix;
import com.kstenschke.realigner.resources.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DialogWrapOptions extends JDialog {

	private JButton buttonCancel;
	private JButton buttonOK;
	private JButton buttonUnwrap;
	private JPanel contentPane;
	private JPanel panelWrapButtonsContainer;
	private JTextField textFieldPostfix;
	private JTextField textFieldPrefix;
	private JPanel quickWrapButtonsPanel;
    private JButton buttonSave;
    private JPanel defaultPanel;
    private JPanel panelMainButtons;
    public JRadioButton quickUnwrapRadioButton;
    public JRadioButton quickWrapRadioButton;
    public JRadioButton wholeSelectionRadioButton;
    public JRadioButton eachLineRadioButton;
    private JPanel panelMultiLineOptions;
    private JLabel labelQuickWraps;
    private JLabel labelWrap;

    // Wrap modes
    private static final int MODE_WRAP_EACH_LINE = 0;
    public static final int MODE_WRAP_WHOLE      = 1;

        // Operations
    private static final int OPERATION_CANCEL    = 0;
	public static final int OPERATION_WRAP       = 1;
	public static final int OPERATION_UNWRAP     = 2;

	public Integer clickedOperation = OPERATION_CANCEL;

	/**
	 * Constructor
	 */
	public DialogWrapOptions(Boolean isMultiLineSelection) {
		clickedOperation = OPERATION_CANCEL;

		setContentPane(contentPane);
		setModal(true);
        setResizable(false);
		getRootPane().setDefaultButton(buttonOK);

        initIcons();
        initQuickWrapButtons();

        panelMultiLineOptions.setVisible(isMultiLineSelection);
        textFieldPrefix.addFocusListener( new FocusListenerPrefix(textFieldPrefix, textFieldPostfix));

    		// Add button action listeners
        buttonSave.addActionListener(this.getActionListenerSaveQuickWrapButton());
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

            // Add listeners to radio buttons
        eachLineRadioButton.addActionListener(this.getActionListenerMulitLineModeRadio(MODE_WRAP_EACH_LINE));
        wholeSelectionRadioButton.addActionListener(this.getActionListenerMulitLineModeRadio(MODE_WRAP_WHOLE));
        quickWrapRadioButton.addActionListener(this.getActionListenerQuickModeRadio(OPERATION_WRAP));
        quickUnwrapRadioButton.addActionListener( this.getActionListenerQuickModeRadio(OPERATION_UNWRAP) );

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

    /**
     * @return  Integer     Wrap each line / whole multi-line selection
     */
    public Integer getWrapMode() {
        return eachLineRadioButton.isSelected() ? MODE_WRAP_EACH_LINE : MODE_WRAP_WHOLE;
    }

    private void initIcons() {
        labelWrap.setIcon( Icons.ICON_WRAP );
        buttonSave.setIcon( Icons.ICON_BOOKMARK_ADD );
        labelQuickWraps.setIcon( Icons.ICON_BOOKMARKS );
    }

    /**
     * Init quick wrap buttons from stored wrap button item configs, or hide resp. sub panel
     */
    private void initQuickWrapButtons() {
        if ( !SettingsQuickWraps.areWrapButtonsConfigured() ) {
            quickWrapButtonsPanel.setVisible(false);
        } else {
                // Create, add and show quick wrap buttons
            Object[] allButtonsLabels           = SettingsQuickWraps.getAllWrapButtonLabels();
            Object[] allButtonPrefixConfigs     = SettingsQuickWraps.getAllWrapButtonPrefixes();
            Object[] allButtonPostfixConfigs    = SettingsQuickWraps.getAllWrapButtonPostfixes();

                // Cleanup wrap buttons panel, set layout: grid with a row per quick wrap button
            panelWrapButtonsContainer.removeAll();
            panelWrapButtonsContainer.setLayout( new GridLayoutManager(allButtonsLabels.length, 1, new Insets(0, 0, 0, 0), 0, 0, true, false) );

            this.addQuickWrapButtons(allButtonsLabels, allButtonPrefixConfigs, allButtonPostfixConfigs);

            panelWrapButtonsContainer.revalidate();
            quickWrapButtonsPanel.setVisible(true);
        }
    }

    /**
     * @return  ActionListener
     */
    private ActionListener getActionListenerSaveQuickWrapButton() {
        final DialogWrapOptions dialog = this;

        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String prefix = getPrefix();
                String postfix= getPostfix();

                SettingsQuickWraps.saveWrapButtonItemToStore(prefix + "..." + postfix, prefix, postfix);

                dialog.refreshQuickWrapButtons();
            }
        };
    }

    /**
     * @param   mode    Un/wrap
     * @return  ActionListener
     */
    private ActionListener getActionListenerMulitLineModeRadio(final Integer mode) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences.saveMultiLineWrapMode(mode);
            }
        };
    }

    /**
     * @param   mode    Un/wrap
     * @return  ActionListener
     */
    private ActionListener getActionListenerQuickModeRadio(final Integer mode) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences.saveQuickWrapMode(mode);
            }
        };
    }

    /**
     * @param   allButtonsLabels
     * @param   allButtonPrefixConfigs
     * @param   allButtonPostfixConfigs
     */
    private void addQuickWrapButtons(Object[] allButtonsLabels, Object[] allButtonPrefixConfigs, Object[] allButtonPostfixConfigs) {
        for (int i = 0; i < allButtonsLabels.length; i++) {
            String buttonLabel = allButtonsLabels[i].toString();
            JButton wrapButton = new JButton(buttonLabel);
            panelWrapButtonsContainer.add(wrapButton, new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

                // Add button action
            final DialogWrapOptions dialog    = this;
            final String prefix = allButtonPrefixConfigs[i].toString();
            final String postfix = allButtonPostfixConfigs[i].toString();

            wrapButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        // Perform Un/Wrap
                    setTextFieldPrefix(prefix);
                    setTextFieldPostfix(postfix);

                    if( dialog.quickWrapRadioButton.isSelected() ) {
                        onOK();
                    } else {
                        onUnwrap();
                    }
                }
            });

                // Add Context menu
            PopupWrapButton popupWrapButton = new PopupWrapButton(wrapButton, this);
            wrapButton.addMouseListener( popupWrapButton.getPopupListener() );
        }
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
	public String getPrefix() {
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
	public String getPostfix() {
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
		DialogWrapOptions dialog = new DialogWrapOptions(true);

		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}

    /**
     * Refresh quickWrap buttons and resize the dialog to fit them in
     */
    public void refreshQuickWrapButtons() {
        this.initQuickWrapButtons();
        this.pack();
    }

}
