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


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.kstenschke.realigner.Preferences;
import com.kstenschke.realigner.TextualHelper;
import com.kstenschke.realigner.resources.forms.WrapOptions;

public class Wrapper {

	Editor editor;

	private Boolean hasSelection = false;

	private Boolean hasMultiLineSelection = false;

	SelectionModel selectionModel;

	Document document;

	int offsetStart, offsetEnd, lineNumberSelStart, lineNumberSelEnd;


	/**
	 * Constructor
	 *
	 * @param   editor   The editor
	 */
	public Wrapper(Editor editor) {
		this.editor   = editor;

		this.hasMultiLineSelection = false;

		this.document = editor.getDocument();

		this.selectionModel = editor.getSelectionModel();
		this.hasSelection = selectionModel.hasSelection();

		if (this.hasSelection) {
			this.offsetStart	= this.selectionModel.getSelectionStart();
			this.offsetEnd	= this.selectionModel.getSelectionEnd();

			this.lineNumberSelStart	= this.document.getLineNumber(this.offsetStart);
			this.lineNumberSelEnd	= this.document.getLineNumber(this.offsetEnd);

			this.hasMultiLineSelection = ( this.lineNumberSelEnd > this.lineNumberSelStart );
		}
	}



	public WrapOptions showWrapOptions() {
		WrapOptions wrapOptionsDialog	= new WrapOptions();
		wrapOptionsDialog.pack();
		wrapOptionsDialog.setLocationRelativeTo(null); // Center to screen
		wrapOptionsDialog.setTitle("Wrap");


		if (this.hasMultiLineSelection) {
			wrapOptionsDialog.setRemoveBlankLinesVisible(true);
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

		return wrapOptionsDialog;
	}



	public Boolean wrap(WrapOptions wrapOptionsDialog) {
		String prefix	= wrapOptionsDialog.getTextFieldPrefix();
		String postfix	= wrapOptionsDialog.getTextFieldPostfix();

		Boolean escapeSingleQuotes	= wrapOptionsDialog.isSelectedEscapeSingleQuotes();
		Boolean escapeDoubleQuotes	= wrapOptionsDialog.isSelectedEscapeDoubleQuotes();
		Boolean escapeBackslashes	= wrapOptionsDialog.isSelectedEscapeBackslashes();

		// Store preferences
		Preferences.saveWrapProperties(prefix, postfix, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);

		int prefixLen	= prefix.length();
//						int postfixLen	= postfix.length();

		if (hasSelection) {
			offsetStart	= selectionModel.getSelectionStart();
			offsetEnd	= selectionModel.getSelectionEnd();

			lineNumberSelStart	= document.getLineNumber(offsetStart);
			lineNumberSelEnd	= document.getLineNumber(offsetEnd);

			if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
				lineNumberSelEnd--;
			}

			CharSequence editorText = document.getCharsSequence();

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

		return true;
	}


}
