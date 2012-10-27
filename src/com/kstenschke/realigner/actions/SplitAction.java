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

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.kstenschke.realigner.Preferences;
import com.kstenschke.realigner.TextualHelper;
import com.kstenschke.realigner.resources.forms.SplitOptions;

import javax.swing.*;


/**
 * Implode / Explode Action
 */
public class SplitAction extends AnAction {

	/**
	 * Disable when no project open
	 *
	 * @param event		Action system event
	 */
	public void update( AnActionEvent event ) {
		event.getPresentation().setEnabled(event.getData(PlatformDataKeys.EDITOR) != null);
	}



	/**
	 * Perform split into lines
	 *
	 * @param	event	Action system event
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
						final Document document = editor.getDocument();
						SelectionModel selectionModel = editor.getSelectionModel();
						boolean hasSelection = selectionModel.hasSelection();

							// Setup dialog
						SplitOptions splitOptionsDialog  = new SplitOptions();
						splitOptionsDialog.pack();
						splitOptionsDialog.setLocationRelativeTo(null); // center to screen
						splitOptionsDialog.setTitle("Split by Delimiter");

							// Load and init dialog options from preferences
						splitOptionsDialog.setDelimiter(Preferences.getSplitDelimiter());
						splitOptionsDialog.setDelimiterDisposalMethod( Integer.parseInt(Preferences.getSplitWhere()) );

							// Show dialog
						splitOptionsDialog.setVisible(true);

							// Clicked ok: conduct split
						if( splitOptionsDialog.clickedOk ) {
							String delimiter = splitOptionsDialog.getDelimiter();
							Integer delimiterDisposalMethod = splitOptionsDialog.getDelimiterDisposalMethod();

							Preferences.saveSplitProperties(delimiter, delimiterDisposalMethod);

							if( delimiter != null && delimiter.length() > 0 ) {
							if (hasSelection) {
								int offsetStart	= selectionModel.getSelectionStart();
								int offsetEnd	= selectionModel.getSelectionEnd();

									// Explode all selected lines by delimiter
								CharSequence editorText	= document.getCharsSequence();
								String selectedText		= TextualHelper.getSubString(editorText, offsetStart, offsetEnd);

								if( !selectedText.contains(delimiter) ) {
									JOptionPane.showMessageDialog(null, "Delimiter not found.");
								} else {
									int lineStart	= document.getLineNumber( selectionModel.getSelectionStart() );
									int lineEnd		= document.getLineNumber( selectionModel.getSelectionEnd() );

									for( int lineNumber = lineEnd; lineNumber >= lineStart; lineNumber--) {
										int offsetLineStart	= document.getLineStartOffset(lineNumber);
										String lineText		= TextualHelper.extractLine(document, lineNumber);
										int offsetLineEnd	= offsetLineStart + lineText.length() - 1;

										String replacement = getSplitReplacementByDelimiterDisposalMethod(delimiter, delimiterDisposalMethod);
										String explodedText  = lineText.replace(delimiter, replacement);
										document.replaceString(offsetLineStart, offsetLineEnd, explodedText);
									}
								}
							} else {
									// Explode line containing the caret by delimiter
								int caretOffset	= editor.getCaretModel().getOffset();
								int lineNumber	= document.getLineNumber(caretOffset);

								int offsetLineStart	= document.getLineStartOffset(lineNumber);
								String lineText		= TextualHelper.extractLine(document, lineNumber);
								int offsetLineEnd	= offsetLineStart + lineText.length() - 1;

								if( !lineText.contains(delimiter) ) {
									JOptionPane.showMessageDialog(null, "Delimiter not found.");
								} else {
									String replacement	= getSplitReplacementByDelimiterDisposalMethod(delimiter, delimiterDisposalMethod);
									String explodedText	= lineText.replace(delimiter, replacement);
									document.replaceString(offsetLineStart, offsetLineEnd, explodedText);
								}
							}
						}
					}
				}
			}
		});

		}}, "Split into Lines", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}



	/**
	 * Get split replacement string, according to given delimiter and delimiter disposal method
	 *
	 * @param	delimiter		Delimiter string
	 * @param	disposalMethod	Before/At/After
	 * @return					String
	 */
	private String getSplitReplacementByDelimiterDisposalMethod(String delimiter, Integer disposalMethod) {
		if( disposalMethod == SplitOptions.METHOD_DELIMITER_DISPOSAL_BEFORE) {
			return "\n" + delimiter;
		} else if( disposalMethod == SplitOptions.METHOD_DELIMITER_DISPOSAL_AFTER) {
				return delimiter + "\n";
		}

		return "\n";
	}

}
