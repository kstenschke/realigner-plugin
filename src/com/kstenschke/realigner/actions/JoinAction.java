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
package com.kstenschke.realigner.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.kstenschke.realigner.*;
import com.kstenschke.realigner.listeners.ComponentListenerDialog;
import com.kstenschke.realigner.models.Joiner;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogJoinOptions;
import com.kstenschke.realigner.utils.UtilsEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class JoinAction extends AnAction {
    private Project project;
    private Editor editor;

    /**
     * Disable when no project open
     *
     * @param   event   Action system event
     */
    public void update(@NotNull AnActionEvent event) {
        project = event.getData(PlatformDataKeys.PROJECT);
        editor  = event.getData(PlatformDataKeys.EDITOR);

        boolean enabled = project != null && editor != null && canEnable();
        event.getPresentation().setEnabled(enabled);
    }

    private boolean canEnable() {
        SelectionModel selectionModel = editor.getSelectionModel();
        if (!selectionModel.hasSelection()) {
            return false;
        }

        Document document = editor.getDocument();

        int lineNumberSelStart  = document.getLineNumber(selectionModel.getSelectionStart());
        int lineNumberSelEnd    = document.getLineNumber(selectionModel.getSelectionEnd());

        return lineNumberSelEnd > lineNumberSelStart;
    }

    /**
     * Perform implode / explode (= join / split)
     *
     * @param   event   Action system event
     */
    public void actionPerformed(@NotNull final AnActionEvent event) {
        if (null == editor) {
            return;
        }

        boolean cannotJoin = true;

        SelectionModel selectionModel = editor.getSelectionModel();
        boolean hasSelection = selectionModel.hasSelection();

        if (hasSelection) {
            int offsetStart = selectionModel.getSelectionStart();
            int offsetEnd = selectionModel.getSelectionEnd();

            final Document document = editor.getDocument();

            int lineNumberSelStart = document.getLineNumber(offsetStart);
            int lineNumberSelEnd = document.getLineNumber(offsetEnd);
            if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
                lineNumberSelEnd--;
            }
            final int lineNumberSelEndFin = lineNumberSelStart;

            if (lineNumberSelEnd > lineNumberSelStart) {
                DialogJoinOptions optionsDialog = showOptionsDialog();
                if (optionsDialog.clickedOk) {
                    String glue = optionsDialog.textFieldGlue.getText();
                    if (null != glue) {
                        Preferences.saveJoinProperties(glue);
                        Joiner joiner = new Joiner();
                        CommandProcessor.getInstance().executeCommand(
                                project,
                                () -> joiner.joinLines(document, lineNumberSelStart, lineNumberSelEndFin, glue),
                                StaticTexts.UNDO_HISTORY_JOIN,
                                UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
                    }
                    cannotJoin = false;
                }
            }
        }

        // No selection or only one line of selection? Display resp. message
        if (cannotJoin) {
            JOptionPane.showMessageDialog(editor.getComponent(), StaticTexts.NOTIFICATION_JOIN_NO_LINES_SELECTED);
        }
    }

    /**
     * Setup and display options dialog for split action
     *
     * @return Split options dialog
     */
    private DialogJoinOptions showOptionsDialog() {
        DialogJoinOptions optionsDialog = new DialogJoinOptions();

        // Load and init dialog options from preferences
        optionsDialog.setGlue(Preferences.getJoinGlue());

        optionsDialog.addComponentListener(new ComponentListenerDialog(Preferences.ID_DIALOG_JOIN));
        UtilsEnvironment.setDialogVisible(editor, Preferences.ID_DIALOG_JOIN, optionsDialog, StaticTexts.MESSAGE_TITLE_JOIN);

        return optionsDialog;
    }
}
