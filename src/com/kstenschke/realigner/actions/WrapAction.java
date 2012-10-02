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
import com.kstenschke.realigner.helpers.TextualHelper;
import com.kstenschke.realigner.helpers.Preferences;
import com.kstenschke.realigner.resources.WrapOptions;


/**
 * Wrap Action
 */
public class WrapAction extends AnAction {

	/**
	 * Disable when no project open
	 *
	 * @param	event	Action system event
	 */
	public void update( AnActionEvent event ) {
		event.getPresentation().setEnabled(event.getData(PlatformDataKeys.EDITOR) != null);
	}



	/**
	 * Perform wrap: show options dialog, than wrap current selection or line of caret or each of the selected lines
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
					WrapOptions wrapOptionsDialog	= new WrapOptions();
					wrapOptionsDialog.pack();
					wrapOptionsDialog.setLocationRelativeTo(null); // Center to screen
					wrapOptionsDialog.setTitle("Wrap");

						// show option "remove blank lines" only when there's a multi-line selection
					final Document document = editor.getDocument();
					CharSequence editorText = document.getCharsSequence();

					SelectionModel selectionModel = editor.getSelectionModel();
					boolean hasSelection = selectionModel.hasSelection();

					int offsetStart, offsetEnd, lineNumberSelStart, lineNumberSelEnd;

					if (hasSelection) {
						offsetStart	= selectionModel.getSelectionStart();
						offsetEnd	= selectionModel.getSelectionEnd();

						lineNumberSelStart	= document.getLineNumber(offsetStart);
						lineNumberSelEnd	= document.getLineNumber(offsetEnd);

						Boolean hasMultiLineSelection = ( lineNumberSelEnd > lineNumberSelStart );
						wrapOptionsDialog.setRemoveBlankLinesVisible(hasMultiLineSelection);
					} else {
						wrapOptionsDialog.setRemoveBlankLinesVisible(false);
					}

						// Load and init from preferences
					wrapOptionsDialog.setTextFieldPrefix(Preferences.getWrapPrefix());
					wrapOptionsDialog.setTextFieldPostfix(Preferences.getWrapPostfix());
					wrapOptionsDialog.setSelectedEscapeSingleQuotes(Preferences.getWrapEscapeSingleQuotes());
					wrapOptionsDialog.setSelectedEscapeDoubleQuotes(Preferences.getWrapEscapeDoubleQuotes());
					wrapOptionsDialog.setSelectedEscapeBackslashes(Preferences.getWrapEscapeBackslashes());
					wrapOptionsDialog.setSelectedRemoveBlankLines(Preferences.getWrapRemoveBlankLines());

					wrapOptionsDialog.setVisible(true);

					if( wrapOptionsDialog.clickedOk ) {
						String prefix	= wrapOptionsDialog.getTextFieldPrefix();
						String postfix	= wrapOptionsDialog.getTextFieldPostfix();

						Boolean escapeSingleQuotes	= wrapOptionsDialog.isSelectedEscapeSingleQuotes();
						Boolean escapeDoubleQuotes	= wrapOptionsDialog.isSelectedEscapeDoubleQuotes();
						Boolean escapeBackslashes	= wrapOptionsDialog.isSelectedEscapeBackslashes();

							// Store preferences
						Preferences.saveWrapProperties(prefix, postfix, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);

						int prefixLen	= prefix.length();
						int postfixLen	= postfix.length();

						if (hasSelection) {
							offsetStart	= selectionModel.getSelectionStart();
							offsetEnd	= selectionModel.getSelectionEnd();

							lineNumberSelStart	= document.getLineNumber(offsetStart);
							lineNumberSelEnd	= document.getLineNumber(offsetEnd);

							if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
								lineNumberSelEnd--;
							}

								// Selection within same line: wrap it
							if( lineNumberSelStart == lineNumberSelEnd ) {
								String selectedText	= TextualHelper.getSubString(editorText, offsetStart, offsetEnd);
								selectedText		= TextualHelper.escapeSelectively(selectedText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);

								String wrappedString = prefix + selectedText + postfix;
								document.replaceString(offsetStart, offsetEnd, wrappedString);

									// Update selection
								selectionModel.setSelection(offsetStart, offsetStart + wrappedString.length());
							} else {
								// Selection of multiple lines

									// Remove blank lines option activated? find and remove em
								if( wrapOptionsDialog.isSelectedRemoveBlankLines() ) {
									String selectedText	= TextualHelper.getSubString(editorText, offsetStart, offsetEnd);

									int amountBlankLines	= TextualHelper.getAmountMatches(selectedText, "\\n(\\s)*\\n");
									String selectedTextNoBlankLines	= selectedText.replaceAll("\\n(\\s)*\\n", "\n");
									document.replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), selectedTextNoBlankLines);

										// Adjust selection
									selectionModel.setSelection(document.getLineStartOffset(lineNumberSelStart), document.getLineEndOffset(lineNumberSelEnd - amountBlankLines));
								}

									// Wrap each line, begin/end at selection offsets
								lineNumberSelEnd	= document.getLineNumber( selectionModel.getSelectionEnd() );

								for(int lineNumber = lineNumberSelEnd; lineNumber >= lineNumberSelStart; lineNumber--) {
									int offsetLineStart	= document.getLineStartOffset(lineNumber);
									String lineText		= TextualHelper.extractLine(document, lineNumber);
									int offsetLineEnd	= offsetLineStart + lineText.length() - 1;

									document.insertString(offsetLineEnd, postfix);
									document.insertString(offsetLineStart, prefix);

									lineText	= lineText.replaceAll("\n", "");
									lineText	= TextualHelper.escapeSelectively(lineText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
									document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);
								}

									// Update selection: all lines of selection fully
								selectionModel.setSelection(document.getLineStartOffset(lineNumberSelStart), document.getLineEndOffset(lineNumberSelEnd));
							}
						} else {
								// No selection: wrap the line where the caret is
							int caretOffset	= editor.getCaretModel().getOffset();
							int lineNumber	= document.getLineNumber(caretOffset);

							int offsetLineStart	= document.getLineStartOffset(lineNumber);
							String lineText		= TextualHelper.extractLine(document, lineNumber);
							int offsetLineEnd	= offsetLineStart + lineText.length() - 1;

							document.insertString(offsetLineEnd, postfix);
							document.insertString(offsetLineStart, prefix);

							lineText	= lineText.replaceAll("\n", "");
							lineText	= TextualHelper.escapeSelectively(lineText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
							document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);

								// Update selection: whole line
							selectionModel.setSelection(document.getLineStartOffset(lineNumber), document.getLineEndOffset(lineNumber));
						}
					}
				}
			}
		});

		}}, "Wrap", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}

}
