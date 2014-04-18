package com.kstenschke.realigner.resources.forms;

import javax.swing.*;
import java.awt.event.*;

public class DialogJoinOptions extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JTextField textFieldGlue;

    public Boolean clickedOk = false;

    /**
     * Constructor
     */
    public DialogJoinOptions() {
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
