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
import com.kstenschke.realigner.resources.SplitOptions;
import com.kstenschke.realigner.resources.WrapOptions;

import javax.swing.*;


/**
 * Wrap Action
 */
public class WrapAction extends AnAction {

	/**
	 * @param	event	Action system event
	 */
	public void update(AnAction event) {
//        event.getPresentation().setEnabled(event.getDataContext().getData(DataConstants.EDITOR) != null);
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

					WrapOptions wrapOptionsDialog  = new WrapOptions();
					wrapOptionsDialog.pack();
					wrapOptionsDialog.setLocationRelativeTo(null); // center to screen
					wrapOptionsDialog.setTitle("Wrap");
					wrapOptionsDialog.setVisible(true);

					//String wrap = Messages.showInputDialog(
					//	currentProject, "Wrap with:", "Wrap",
					//	IconLoader.getIcon("/com/kstenschke/realigner/resources/wrap-tight.png"),
					//   "\"|\"", null
					//);

					//if( wrap != null && wrap.length() > 0 ) {
					if( wrapOptionsDialog.clickedOk ) {
						String prefix	= wrapOptionsDialog.getTextFieldPrefix();
						String postfix	= wrapOptionsDialog.getTextFieldPostfix();
						int prefixLen	= prefix.length();
						int postfixLen	= postfix.length();

						Boolean escapeSingleQuotes	= wrapOptionsDialog.isSelectedEscapeSingleQuotes();
						Boolean escapeDoubleQuotes	= wrapOptionsDialog.isSelectedEscapeDoubleQuotes();
						Boolean escapeBackslashes	= wrapOptionsDialog.isSelectedEscapeBackslashes();

						final Document document = editor.getDocument();
						CharSequence editorText = document.getCharsSequence();
						SelectionModel selectionModel = editor.getSelectionModel();
						boolean hasSelection = selectionModel.hasSelection();

						if (hasSelection) {
							int offsetStart   = selectionModel.getSelectionStart();
							int offsetEnd     = selectionModel.getSelectionEnd();

							int lineNumberSelStart = document.getLineNumber(offsetStart);
							int lineNumberSelEnd = document.getLineNumber(offsetEnd);

							if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
								lineNumberSelEnd--;
							}

								// Selection within same line: wrap it
							if( lineNumberSelStart == lineNumberSelEnd ) {
								String selectedText	= TextualHelper.getSubString(editorText, offsetStart, offsetEnd);
								selectedText		= TextualHelper.escapeSelectively(selectedText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);

								String wrappedString = prefix + selectedText + postfix;
								document.replaceString(offsetStart, offsetEnd, wrappedString);
							} else {
								// Selection of multiple lines: wrap each line, begin/end at selection offsets
								for(int lineNumber = lineNumberSelEnd; lineNumber >= lineNumberSelStart; lineNumber--) {
									int offsetLineStart  = document.getLineStartOffset(lineNumber);
									String lineText      = TextualHelper.extractLine(document, lineNumber);
									int offsetLineEnd    = offsetLineStart + lineText.length() - 1;

									document.insertString(offsetLineEnd, postfix);
									document.insertString(offsetLineStart, prefix);

									lineText	= lineText.replaceAll("\n", "");
									lineText	= TextualHelper.escapeSelectively(lineText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
									document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);
								}
							}
						} else {
							// No selection: wrap the line where the caret is
							int caretOffset   = editor.getCaretModel().getOffset();
							int lineNumber     = document.getLineNumber(caretOffset);

							int offsetLineStart  = document.getLineStartOffset(lineNumber);
							String lineText      = TextualHelper.extractLine(document, lineNumber);
							int offsetLineEnd    = offsetLineStart + lineText.length() - 1;

							document.insertString(offsetLineEnd, postfix);
							document.insertString(offsetLineStart, prefix);

							lineText	= lineText.replaceAll("\n", "");
							lineText	= TextualHelper.escapeSelectively(lineText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
							document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);
						}
					}
				}
			}
		});

      }}, "Wrap", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}

}
