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

package com.kstenschke.realigner.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.kstenschke.realigner.helpers.TextualHelper;

import java.util.List;


/**
 * Implode / Explode Action
 */
public class JoinAction extends AnAction {

	public void update(AnAction event) {
//        event.getPresentation().setEnabled(event.getDataContext().getData(DataConstants.EDITOR) != null);
	}


	/**
	 * Perform implode / explode
	 *
	 * @param event
	 * @return void.
	 */
	public void actionPerformed(final AnActionEvent event) {

		final Project currentProject = event.getData(PlatformDataKeys.PROJECT);
		//Project currentProject = (Project) event.getDataContext().getData(DataConstants.PROJECT);

		CommandProcessor.getInstance().executeCommand(currentProject, new Runnable() {
			public void run() {

				ApplicationManager.getApplication().runWriteAction(new Runnable() {
					public void run() {
					Editor editor = event.getData(PlatformDataKeys.EDITOR);
					//Editor editor = (Editor) event.getDataContext().getData(DataConstants.EDITOR);

				if (editor != null) {
					SelectionModel selectionModel = editor.getSelectionModel();
					boolean hasSelection = selectionModel.hasSelection();

					if (hasSelection) {
						int offsetStart   = selectionModel.getSelectionStart();
						int offsetEnd     = selectionModel.getSelectionEnd();

						final Document document = editor.getDocument();
						//CharSequence editorText = document.getCharsSequence();
						//int caretOffset = editor.getCaretModel().getOffset();

						int lineNumberSelStart = document.getLineNumber(offsetStart);
						int lineNumberSelEnd = document.getLineNumber(offsetEnd);

						if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
							lineNumberSelEnd--;
						}

						if( lineNumberSelEnd > lineNumberSelStart ) {
								// Join selected lines
							String glue = Messages.showInputDialog(
								currentProject,
								"Enter Glue (Optional)", "Join Lines with Glue",
								IconLoader.getIcon("/com/kstenschke/realigner/icons/arrow-join.png"),
								", ", null
							);

							if( glue != null ) {
								List<String> linesList   = TextualHelper.extractLines(document, lineNumberSelStart, lineNumberSelEnd);
								String linesStr   =  "";
								int amountLines   = linesList.size();
								for(int i = 0; i < amountLines; i++) {
									linesStr = linesStr + linesList.get(i) + ( i < (amountLines -1) ? glue : "");
								}

									// Remove newlines
								String joinedLines   = linesStr.replaceAll("(\\n)+", "" );

									// Replace the full lines with themselves joined
								offsetStart          = document.getLineStartOffset(lineNumberSelStart);
								offsetEnd            = document.getLineEndOffset(lineNumberSelEnd);

								document.replaceString(offsetStart, offsetEnd, joinedLines);
							}
						}
						// Don't join if there's no selection or only one line of selection
					}
				}

			}
		});

            }}, "Join Lines with Glue", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}

}
