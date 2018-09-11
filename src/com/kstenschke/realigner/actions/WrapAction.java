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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.kstenschke.realigner.Preferences;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogWrapOptions;
import org.jetbrains.annotations.NotNull;

/**
 * Wrap Action
 */
class WrapAction extends AnAction {

    /**
     * Disable when no project open
     *
     * @param event Action system event
     */
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(event.getData(PlatformDataKeys.EDITOR) != null);
    }

    /**
     * Perform wrap or unwrap
     * Show options dialog, than wrap/unwrap current selection or line of caret or each of the selected lines
     *
     * @param event Action system event
     */
    public void actionPerformed(@NotNull final AnActionEvent event) {
        final Project currentProject = event.getData(PlatformDataKeys.PROJECT);

        ApplicationManager.getApplication().runWriteAction(() -> {
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            if (null == editor) {
                return;
            }

            final Wrapper wrapper = new Wrapper(editor);
            final boolean isSelectionMultiLine = wrapper.isSelectionMultiLine;
            DialogWrapOptions optionsDialog = wrapper.getWrapOptionsDialog(isSelectionMultiLine);

            final String prefix     = optionsDialog.getPrefix();
            final String postfix    = optionsDialog.getPostfix();
            final Integer wrapMode  = optionsDialog.getWrapMode();

            Preferences.saveWrapProperties(prefix, postfix);

            int operation = optionsDialog.operation;
            if (DialogWrapOptions.OPERATION_AUTODETECT == optionsDialog.operation) {
                operation   = wrapper.isWrapped(prefix, postfix) ? DialogWrapOptions.OPERATION_UNWRAP : DialogWrapOptions.OPERATION_WRAP;
            }

            // Un/wrap
            switch (operation) {
                case DialogWrapOptions.OPERATION_WRAP:
                    CommandProcessor.getInstance().executeCommand(currentProject, () -> wrapper.wrap(prefix, postfix, wrapMode), StaticTexts.UNDO_HISTORY_WRAP, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
                    break;
                case DialogWrapOptions.OPERATION_UNWRAP:
                    CommandProcessor.getInstance().executeCommand(currentProject, () -> wrapper.unwrap(prefix, postfix), StaticTexts.UNDO_HISTORY_UNWRAP, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
                    break;
            }

            optionsDialog.makeFiredButtonTopMost();
        });
    }
}
