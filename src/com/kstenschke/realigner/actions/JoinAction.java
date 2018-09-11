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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.kstenschke.realigner.*;
import com.kstenschke.realigner.listeners.ComponentListenerDialog;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogJoinOptions;
import com.kstenschke.realigner.utils.UtilsEnvironment;
import com.kstenschke.realigner.utils.UtilsTextual;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * Implode / Explode Action
 */
class JoinAction extends AnAction {

    private Project project;
    private Editor editor;

	/**
	 * Disable when no project open
	 *
	 * @param   event   Action system event
	 */
	public void update(@NotNull AnActionEvent event) {
		boolean enabled = false;
		this.project    = event.getData(PlatformDataKeys.PROJECT);
		this.editor     = event.getData(PlatformDataKeys.EDITOR);
		if (this.project != null && this.editor != null) {
			SelectionModel selectionModel = this.editor.getSelectionModel();
			if (selectionModel.hasSelection()) {
				final Document document = this.editor.getDocument();

				int lineNumberSelStart  = document.getLineNumber(selectionModel.getSelectionStart());
				int lineNumberSelEnd    = document.getLineNumber(selectionModel.getSelectionEnd());

				if (lineNumberSelEnd > lineNumberSelStart) {
					enabled = true;
				}
			}
		}

		event.getPresentation().setEnabled(enabled);
	}

	/**
	 * Perform implode / explode (= join / split)
	 *
	 * @param   event   Action system event
	 */
	public void actionPerformed(@NotNull final AnActionEvent event) {
		CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(new Runnable() {
			public void run() {
				if (editor != null) {
					boolean cannotJoin = false;

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

						if (lineNumberSelEnd > lineNumberSelStart) {
							DialogJoinOptions optionsDialog = showOptionsDialog();

							if (optionsDialog.clickedOk) {
								String glue = optionsDialog.textFieldGlue.getText();
								if (glue != null) {
									Preferences.saveJoinProperties(glue);
									joinLines(document, lineNumberSelStart, lineNumberSelEnd, glue);
								}
							}
						} else {
							cannotJoin = true;
						}
					} else {
						cannotJoin = true;
					}

					// No selection or only one line of selection? Display resp. message
					if (cannotJoin) {
						JOptionPane.showMessageDialog(editor.getComponent(), StaticTexts.NOTIFICATION_JOIN_NO_LINES_SELECTED);
					}
				}
			}

			/**
			 * @param document
			 * @param lineNumberSelStart
			 * @param lineNumberSelEnd
			 * @param glue
			 */
			private void joinLines(Document document, int lineNumberSelStart, int lineNumberSelEnd, String glue) {
				int offsetStart;
				int offsetEnd;
				List<String> linesList = UtilsTextual.extractLines(document, lineNumberSelStart, lineNumberSelEnd);
				String linesStr = "";
				int amountLines = linesList.size();
				for (int i = 0; i < amountLines; i++) {
					linesStr = linesStr + (i > 0 ? linesList.get(i).trim() : linesList.get(i)) + (i < (amountLines - 1) ? glue : "");
				}

				// Remove newlines
				String joinedLines = linesStr.replaceAll("(\\n)+", "");

                // Replace the full lines with themselves joined
				offsetStart = document.getLineStartOffset(lineNumberSelStart);
				offsetEnd = document.getLineEndOffset(lineNumberSelEnd);

				document.replaceString(offsetStart, offsetEnd, joinedLines);
			}
		}), StaticTexts.UNDO_HISTORY_JOIN, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
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
