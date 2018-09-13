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
import com.kstenschke.realigner.models.Splitter;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogSplitOptions;
import com.kstenschke.realigner.utils.UtilsEnvironment;
import org.jetbrains.annotations.NotNull;

/**
 * Implode / Explode Action
 */
class SplitAction extends AnAction {

    private Editor  editor;

    /**
     * Disable when no project open
     *
     * @param event Action system event
     */
    public void update(@NotNull AnActionEvent event) {
        this.editor = event.getData(PlatformDataKeys.EDITOR);

        event.getPresentation().setEnabled(null != this.editor);
    }

    /**
     * Perform split into lines
     *
     * @param   event   Action system event
     */
    public void actionPerformed(@NotNull final AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        if (null == editor) {
            return;
        }

        final Document document         = editor.getDocument();
        SelectionModel selectionModel   = editor.getSelectionModel();
        boolean hasSelection            = selectionModel.hasSelection();

        // Setup and display options dialog
        DialogSplitOptions optionsDialog = showOptionsDialog();

        // Clicked ok: conduct split
        if (!optionsDialog.clickedOk) {
            return;
        }

        String delimiter       = optionsDialog.getDelimiter();
        boolean trimWhitespace = optionsDialog.getIsSelectedTrimWhitespace();
        Integer delimiterDisposalMethod = optionsDialog.getDelimiterDisposalMethod();

        Preferences.saveSplitProperties(delimiter, trimWhitespace, delimiterDisposalMethod);

        if (null != delimiter && delimiter.length() > 0) {
            Splitter splitter = new Splitter(project, editor);
            if (hasSelection) {
                CommandProcessor.getInstance().executeCommand(
                        project,
                        () -> splitter.splitSelection(document, selectionModel, delimiter, trimWhitespace, delimiterDisposalMethod),
                        StaticTexts.UNDO_HISTORY_SPLIT,
                        UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
            } else {
                CommandProcessor.getInstance().executeCommand(
                        project,
                        () -> splitter.splitLine(document, selectionModel, delimiter, trimWhitespace, delimiterDisposalMethod),
                        StaticTexts.UNDO_HISTORY_SPLIT,
                        UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
            }
        } else if (!hasSelection) {
            Splitter splitter = new Splitter(project, editor);
            CommandProcessor.getInstance().executeCommand(
                    project,
                    () -> splitter.splitLineAtSoftWrap(document),
                    StaticTexts.UNDO_HISTORY_SPLIT,
                    UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
        }
    }

    /**
     * Setup and display options dialog for split action
     *
     * @return Split options dialog
     */
    private DialogSplitOptions showOptionsDialog() {
        DialogSplitOptions optionsDialog = new DialogSplitOptions();

        // Load and init dialog options from preferences
        optionsDialog.setDelimiter(Preferences.getSplitDelimiter());
        optionsDialog.setCheckboxTrimWhitespaceSelected(Preferences.getIsSplitIsSelectedTrimWhitespace());
        optionsDialog.setDelimiterDisposalMethod(Integer.parseInt(Preferences.getSplitWhere()));

        optionsDialog.addComponentListener(new ComponentListenerDialog(Preferences.ID_DIALOG_SPLIT));
        UtilsEnvironment.setDialogVisible(editor, Preferences.ID_DIALOG_SPLIT, optionsDialog, StaticTexts.MESSAGE_TITLE_SPLIT);

        return optionsDialog;
    }
}
