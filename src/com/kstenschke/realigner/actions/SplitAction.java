/*
 * Copyright 2012-2013 Kay Stenschke
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
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
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
	 * @param event Action system event
	 */
	public void update(AnActionEvent event) {
		event.getPresentation().setEnabled(event.getData(PlatformDataKeys.EDITOR) != null);
	}

	/**
	 * Perform split into lines
	 *
	 * @param   event   Action system event
	 */
	public void actionPerformed(final AnActionEvent event) {
		final Project currentProject = event.getData(PlatformDataKeys.PROJECT);

		CommandProcessor.getInstance().executeCommand(currentProject, new Runnable() {
			public void run() {

				ApplicationManager.getApplication().runWriteAction(new Runnable() {
					public void run() {
						Editor editor = event.getData(PlatformDataKeys.EDITOR);

						if (editor != null) {
							final Document document = editor.getDocument();
							SelectionModel selectionModel = editor.getSelectionModel();
							boolean hasSelection = selectionModel.hasSelection();

							// Setup and display options dialog
							SplitOptions splitOptionsDialog = showOptionsDialog();

							// Clicked ok: conduct split
							if (splitOptionsDialog.clickedOk) {
								String delimiter = splitOptionsDialog.getDelimiter();
								Integer delimiterDisposalMethod = splitOptionsDialog.getDelimiterDisposalMethod();

								Preferences.saveSplitProperties(delimiter, delimiterDisposalMethod);

								if (delimiter != null && delimiter.length() > 0) {
									if (hasSelection) {
										int offsetStart = selectionModel.getSelectionStart();
										int offsetEnd = selectionModel.getSelectionEnd();

										// Explode all selected lines by delimiter
										CharSequence editorText = document.getCharsSequence();
										String selectedText = TextualHelper.getSubString(editorText, offsetStart, offsetEnd);

										if (!selectedText.contains(delimiter)) {
											JOptionPane.showMessageDialog(null, "Delimiter not found.");
										} else {
											int lineStart = document.getLineNumber(selectionModel.getSelectionStart());
											int lineEnd = document.getLineNumber(selectionModel.getSelectionEnd());

											for (int lineNumber = lineEnd; lineNumber >= lineStart; lineNumber--) {
												int offsetLineStart = document.getLineStartOffset(lineNumber);
												String lineText = TextualHelper.extractLine(document, lineNumber);
												int offsetLineEnd = offsetLineStart + lineText.length() - 1;

												String replacement = getSplitReplacementByDelimiterDisposalMethod(delimiter, delimiterDisposalMethod);
												String explodedText = lineText.replace(delimiter, replacement);
												document.replaceString(offsetLineStart, offsetLineEnd, explodedText);
											}
										}
									} else {
										// Explode line containing the caret by delimiter
										int caretOffset = editor.getCaretModel().getOffset();
										int lineNumber = document.getLineNumber(caretOffset);

										int offsetLineStart = document.getLineStartOffset(lineNumber);
										String lineText = TextualHelper.extractLine(document, lineNumber);
										int offsetLineEnd = offsetLineStart + lineText.length() - 1;

										if (!lineText.contains(delimiter)) {
											JOptionPane.showMessageDialog(null, "Delimiter not found.");
										} else {
											String replacement = getSplitReplacementByDelimiterDisposalMethod(delimiter, delimiterDisposalMethod);
											String explodedText = lineText.replace(delimiter, replacement);
											document.replaceString(offsetLineStart, offsetLineEnd, explodedText);
										}
									}
								} else {
									// No selection + empty delimiter = split line at soft-wrap
									if (!hasSelection) {
										int caretOffset = editor.getCaretModel().getOffset();
										int lineNumber = document.getLineNumber(caretOffset);

										int offsetLineStart = document.getLineStartOffset(lineNumber);
										String lineText = TextualHelper.extractLine(document, lineNumber);
										Integer textLength = getTextWidth(lineText, editor);
										if (textLength > 120) {
											int offsetLineEnd = offsetLineStart + lineText.length() - 1;
											int wrapPosition = 120;
											String wrapChar = lineText.substring(wrapPosition, wrapPosition + 1);
											while (wrapPosition > 0 && !wrapChar.equals(" ") && !wrapChar.equals("\t")) {
												wrapPosition--;
												wrapChar = lineText.substring(wrapPosition, wrapPosition + 1);
											}
											if (wrapPosition > 1) {
												String explodedText = lineText.substring(0, wrapPosition) + "\n" + lineText.substring(wrapPosition + 1, lineText.length());
												document.replaceString(offsetLineStart, offsetLineEnd, explodedText);
												editor.getCaretModel().moveToOffset(document.getLineStartOffset(lineNumber + 1));
												editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
											}
										}
									}
								}
							}
						}
					}
				});

			}
		}, "Split into Lines", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}

	/**
	 * Get visual length of given text, that is in editor where tabs resolve to the width of multiple characters
	 *
	 * @param text   Text to be "measured"
	 * @param editor
	 * @return Amount of characters
	 */
	private Integer getTextWidth(String text, Editor editor) {
		Integer length = text.length();

		// Get tab size
		Integer tabSize = 0;

		Project project = editor.getProject();
		PsiFile psifile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
		CommonCodeStyleSettings commonCodeStyleSettings = new CommonCodeStyleSettings(psifile.getLanguage());
		CommonCodeStyleSettings.IndentOptions indentOptions = commonCodeStyleSettings.getIndentOptions();

		if (indentOptions != null) {
			tabSize = commonCodeStyleSettings.getIndentOptions().TAB_SIZE;
		}
		if (tabSize == 0) {
			tabSize = editor.getSettings().getTabSize(editor.getProject());
		}

		Integer amountTabs = TextualHelper.countSubstringOccurrences(text, "\t");

		return length + amountTabs * (tabSize - 1);
	}

	/**
	 * Get split replacement string, according to given delimiter and delimiter disposal method
	 *
	 * @param   delimiter      Delimiter string
	 * @param   disposalMethod   Before/At/After
	 * @return String
	 */
	private String getSplitReplacementByDelimiterDisposalMethod(String delimiter, Integer disposalMethod) {
		if (disposalMethod == SplitOptions.METHOD_DELIMITER_DISPOSAL_BEFORE) {
			return "\n" + delimiter;
		} else if (disposalMethod == SplitOptions.METHOD_DELIMITER_DISPOSAL_AFTER) {
			return delimiter + "\n";
		}

		return "\n";
	}

	/**
	 * Setup and display options dialog for split action
	 *
	 * @return Split options dialog
	 */
	private SplitOptions showOptionsDialog() {
		SplitOptions splitOptionsDialog = new SplitOptions();
		splitOptionsDialog.pack();
		splitOptionsDialog.setLocationRelativeTo(null); // center to screen
		splitOptionsDialog.setTitle("Split by Delimiter");

		// Load and init dialog options from preferences
		splitOptionsDialog.setDelimiter(Preferences.getSplitDelimiter());
		splitOptionsDialog.setDelimiterDisposalMethod(Integer.parseInt(Preferences.getSplitWhere()));

		splitOptionsDialog.setVisible(true);

		return splitOptionsDialog;
	}

}
