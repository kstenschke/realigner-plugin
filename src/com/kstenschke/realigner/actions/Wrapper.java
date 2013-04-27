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

	int offsetSelectionStart, offsetSelectionEnd, lineNumberSelectionStart, lineNumberSelectionEnd;



	/**
	 * Constructor
	 *
	 * @param   editor   The editor
	 */
	public Wrapper(Editor editor) {
		this.editor   = editor;
		this.document = editor.getDocument();

		this.initSelectionProperties();
	}

	/**
	 * Initialize selection related wrapper properties
	 */
	public void initSelectionProperties() {
		this.selectionModel = editor.getSelectionModel();

		this.hasSelection = selectionModel.hasSelection();
		this.hasMultiLineSelection = false;

		if (this.hasSelection) {
			this.offsetSelectionStart  = this.selectionModel.getSelectionStart();
			this.offsetSelectionEnd    = this.selectionModel.getSelectionEnd();

			this.lineNumberSelectionStart = this.document.getLineNumber(this.offsetSelectionStart);
			this.lineNumberSelectionEnd   = this.document.getLineNumber(this.offsetSelectionEnd);

			this.hasMultiLineSelection = ( this.lineNumberSelectionEnd > this.lineNumberSelectionStart);
		}
	}

	/**
	 * Setup and display wrap options dialog
	 *
	 * @return  Wrap options dialog
	 */
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


	/**
	 * Perform wrapping of line around caret, single- or multi-line selection
	 *
	 * @param   prefix
	 * @param   postfix
	 * @param   escapeSingleQuotes
	 * @param   escapeDoubleQuotes
	 * @param   escapeBackslashes
	 * @param   removeBlankLines
	 */
	public void wrap(String prefix, String postfix,
	                 Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes,
	                 Boolean removeBlankLines) {

		if (hasSelection) {
			if (document.getLineStartOffset(lineNumberSelectionEnd) == this.offsetSelectionEnd) {
				lineNumberSelectionEnd--;
			}

			if( lineNumberSelectionStart == lineNumberSelectionEnd) {
				this.wrapSingleLinedSelection(prefix, postfix, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
			} else {
				this.wrapMultiLineSelection(prefix, postfix, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes, removeBlankLines);
			}
		} else {
				// No selection: wrap the line where the caret is
			this.wrapCaretLine(prefix, postfix, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
		}
	}

	/**
	 * Perform unwrapping of line around caret, single- or multi-line selection
	 *
	 * @param   prefix
	 * @param   postfix
	 */
	public void unwrap(String prefix, String postfix) {
		if (hasSelection) {
			if (document.getLineStartOffset(lineNumberSelectionEnd) == this.offsetSelectionEnd) {
				lineNumberSelectionEnd--;
			}

			if( lineNumberSelectionStart == lineNumberSelectionEnd) {
				this.unwrapSingleLinedSelection(prefix, postfix);
			} else {
				//this.unwrapMultiLineSelection(prefix, postfix);
			}
		} else {
			// No selection: wrap the line where the caret is
			//this.unwrapCaretLine(prefix, postfix);
		}
	}

	/**
	 * Wrap selection over multiple lines with given prefix and postfix, do given transformations on selection
	 *
	 * @param prefix
	 * @param postfix
	 * @param escapeSingleQuotes
	 * @param escapeDoubleQuotes
	 * @param escapeBackslashes
	 * @param removeBlankLines
	 */
	private void wrapMultiLineSelection(String prefix, String postfix, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes, Boolean removeBlankLines) {
			// Remove blank lines option activated? find and remove em
		if( removeBlankLines ) {
			CharSequence editorText = document.getCharsSequence();
			String selectedText	   = TextualHelper.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

			int amountBlankLines             = TextualHelper.getAmountMatches(selectedText, "\\n(\\s)*\\n");
			String selectedTextNoBlankLines	= selectedText.replaceAll("\\n(\\s)*\\n", "\n");

			document.replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), selectedTextNoBlankLines);

				// Adjust selection
			selectionModel.setSelection(document.getLineStartOffset(lineNumberSelectionStart), document.getLineEndOffset(lineNumberSelectionEnd - amountBlankLines));
		}

		// Wrap each line, begin/end at selection offsets
		Integer prefixLen = prefix.length();
		lineNumberSelectionEnd = document.getLineNumber( selectionModel.getSelectionEnd() );

		for(int lineNumber = lineNumberSelectionEnd; lineNumber >= lineNumberSelectionStart; lineNumber--) {
			int offsetLineStart	= document.getLineStartOffset(lineNumber);
			String lineText		= TextualHelper.extractLine(document, lineNumber);
			int offsetLineEnd    = offsetLineStart + lineText.length() - 1;

			document.insertString(offsetLineEnd, postfix);
			document.insertString(offsetLineStart, prefix);

			lineText	= lineText.replaceAll("\n", "");
			lineText	= TextualHelper.escapeSelectively(lineText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
			document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);
		}

		// Update selection: all lines of selection fully
		selectionModel.setSelection(document.getLineStartOffset(lineNumberSelectionStart), document.getLineEndOffset(lineNumberSelectionEnd));
	}

	/**
	 * Wrap selection within a single line with given prefix and postfix, do given transformations on selection
	 *
	 * @param prefix
	 * @param postfix
	 * @param escapeSingleQuotes
	 * @param escapeDoubleQuotes
	 * @param escapeBackslashes
	 */
	private void wrapSingleLinedSelection(String prefix, String postfix, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes) {
		CharSequence editorText = document.getCharsSequence();
		String selectedText     = TextualHelper.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);
		selectedText		      = TextualHelper.escapeSelectively(selectedText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);

		String wrappedString = prefix + selectedText + postfix;
		document.replaceString(offsetSelectionStart, offsetSelectionEnd, wrappedString);

		// Update selection
		selectionModel.setSelection(offsetSelectionStart, offsetSelectionStart + wrappedString.length());
	}

	/**
	 * Wrap the line where the caret is with given prefix and postfix, do given transformations on the line
	 *
	 * @param prefix
	 * @param postfix
	 * @param escapeSingleQuotes
	 * @param escapeDoubleQuotes
	 * @param escapeBackslashes
	 */
	private void wrapCaretLine(String prefix, String postfix, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes) {
		int caretOffset= editor.getCaretModel().getOffset();
		int lineNumber = document.getLineNumber(caretOffset);

		int offsetLineStart	= document.getLineStartOffset(lineNumber);
		String lineText		= TextualHelper.extractLine(document, lineNumber);
		int offsetLineEnd    = offsetLineStart + lineText.length() - 1;

		document.insertString(offsetLineEnd, postfix);
		document.insertString(offsetLineStart, prefix);

		lineText	= lineText.replaceAll("\n", "");
		lineText	= TextualHelper.escapeSelectively(lineText, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes);
		Integer prefixLen = prefix.length();

		document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);

		// Update selection: whole line
		selectionModel.setSelection(document.getLineStartOffset(lineNumber), document.getLineEndOffset(lineNumber));
	}

	/**
	 * Remove given strings from the beginning and ending of current selection
	 *
	 * @param prefix
	 * @param postfix
	 */
	private void unwrapSingleLinedSelection(String prefix, String postfix) {
		CharSequence editorText = document.getCharsSequence();
		String unwrappedString = TextualHelper.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

		if( unwrappedString.startsWith(prefix)) {
			unwrappedString   = unwrappedString.substring(prefix.length());
		}

		if( unwrappedString.endsWith(postfix)) {
			unwrappedString   = unwrappedString.substring(0, unwrappedString.length()-postfix.length());
		}

			// Update selected text with unwrapped version, update set selection
		document.replaceString(offsetSelectionStart, offsetSelectionEnd, unwrappedString);
		selectionModel.setSelection(offsetSelectionStart, offsetSelectionStart + unwrappedString.length());
	}
}
