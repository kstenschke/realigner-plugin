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
import com.kstenschke.realigner.StaticTexts;
import com.kstenschke.realigner.UtilsEnvironment;
import com.kstenschke.realigner.UtilsTextual;
import com.kstenschke.realigner.resources.forms.DialogWrapOptions;

public class Wrapper {

	final Editor editor;

	private Boolean hasSelection = false;

	SelectionModel selectionModel;

	final Document document;

	int offsetSelectionStart, offsetSelectionEnd, lineNumberSelectionStart, lineNumberSelectionEnd;

	/**
	 * Constructor
	 *
	 * @param editor The editor
	 */
	public Wrapper(Editor editor) {
		this.editor = editor;
		this.document = editor.getDocument();

		this.initSelectionProperties();
	}

	/**
	 * Initialize selection related wrapper properties
	 */
	public void initSelectionProperties() {
		this.selectionModel = editor.getSelectionModel();

		this.hasSelection = selectionModel.hasSelection();

		if (this.hasSelection) {
			this.offsetSelectionStart = this.selectionModel.getSelectionStart();
			this.offsetSelectionEnd = this.selectionModel.getSelectionEnd();

			this.lineNumberSelectionStart = this.document.getLineNumber(this.offsetSelectionStart);
			this.lineNumberSelectionEnd = this.document.getLineNumber(this.offsetSelectionEnd);
		}
	}

	/**
	 * Setup and display wrap options dialog
	 *
	 * @return Wrap options dialog
	 */
	public DialogWrapOptions showWrapOptions() {
		DialogWrapOptions optionsDialog = new DialogWrapOptions();

            // Load and init from preferences
        optionsDialog.setTextFieldPrefix(Preferences.getWrapPrefix());
        optionsDialog.setTextFieldPostfix(Preferences.getWrapPostfix());

        UtilsEnvironment.setDialogVisible(editor, optionsDialog, StaticTexts.MESSAGE_TITLE_WRAP);

		return optionsDialog;
	}

	/**
	 * Perform wrapping of line around caret, single- or multi-line selection
	 *
	 * @param prefix
	 * @param postfix
	 */
	public void wrap(String prefix, String postfix) {

		if (hasSelection) {
			if (document.getLineStartOffset(lineNumberSelectionEnd) == this.offsetSelectionEnd) {
				lineNumberSelectionEnd--;
			}

			if (lineNumberSelectionStart == lineNumberSelectionEnd) {
				this.wrapSingleLinedSelection(prefix, postfix);
			} else {
				this.wrapMultiLineSelection(prefix, postfix);
			}
		} else {
			    // No selection: wrap the line where the caret is
			this.wrapCaretLine(prefix, postfix);
		}
	}

	/**
	 * Perform unwrapping of line around caret, single- or multi-line selection
	 *
	 * @param prefix
	 * @param postfix
	 */
	public void unwrap(String prefix, String postfix) {
		if (hasSelection) {
			if (document.getLineStartOffset(lineNumberSelectionEnd) == this.offsetSelectionEnd) {
				lineNumberSelectionEnd--;
			}

			if (lineNumberSelectionStart == lineNumberSelectionEnd) {
				this.unwrapSingleLinedSelection(prefix, postfix);
			} else {
				this.unwrapMultiLineSelection(prefix, postfix);
			}
		} else {
			    // No selection: wrap the line where the caret is
			this.unwrapCaretLine(prefix, postfix);
		}
	}

	/**
	 * Wrap selection over multiple lines with given prefix and postfix, do given transformations on selection
	 *
	 * @param prefix
	 * @param postfix
	 */
	private void wrapMultiLineSelection(String prefix, String postfix) {
		    // Wrap each line, begin/end at selection offsets
		Integer prefixLen = prefix.length();
		lineNumberSelectionEnd = document.getLineNumber(selectionModel.getSelectionEnd());

		for (int lineNumber = lineNumberSelectionEnd; lineNumber >= lineNumberSelectionStart; lineNumber--) {
			int offsetLineStart = document.getLineStartOffset(lineNumber);
			String lineText = UtilsTextual.extractLine(document, lineNumber);
			int offsetLineEnd = offsetLineStart + lineText.length() - 1;

			document.insertString(offsetLineEnd, postfix);
			document.insertString(offsetLineStart, prefix);

			lineText = lineText.replaceAll("\n", "");
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
	 */
	private void wrapSingleLinedSelection(String prefix, String postfix) {
		CharSequence editorText = document.getCharsSequence();
		String selectedText = UtilsTextual.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

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
	 */
	private void wrapCaretLine(String prefix, String postfix) {
		int caretOffset = editor.getCaretModel().getOffset();
		int lineNumber = document.getLineNumber(caretOffset);

		int offsetLineStart = document.getLineStartOffset(lineNumber);
		String lineText = UtilsTextual.extractLine(document, lineNumber);
		int offsetLineEnd = offsetLineStart + lineText.length() - 1;

		document.insertString(offsetLineEnd, postfix);
		document.insertString(offsetLineStart, prefix);

		lineText = lineText.replaceAll("\n", "");
		Integer prefixLen = prefix.length();

		document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);

		    // Update selection: whole line
		selectionModel.setSelection(document.getLineStartOffset(lineNumber), document.getLineEndOffset(lineNumber));
	}

	/**
	 * Unwrap the line the caret is in
	 *
	 * @param prefix
	 * @param postfix
	 */
	private void unwrapCaretLine(String prefix, String postfix) {
		int caretOffset = editor.getCaretModel().getOffset();
		int lineNumber = document.getLineNumber(caretOffset);

		int offsetLineStart = document.getLineStartOffset(lineNumber);
		String lineText = UtilsTextual.extractLine(document, lineNumber);
		int offsetLineEnd = offsetLineStart + lineText.length() - 1;

		this.offsetSelectionStart = offsetLineStart;
		this.offsetSelectionEnd = offsetLineEnd;

		this.unwrapSingleLinedSelection(prefix, postfix);
	}

	/**
	 * Remove given strings from the beginning and ending of current selection
	 *
	 * @param prefix
	 * @param postfix
	 */
	private void unwrapSingleLinedSelection(String prefix, String postfix) {
		CharSequence editorText = document.getCharsSequence();
		String unwrappedString = UtilsTextual.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

		unwrappedString = UtilsTextual.unwrap(unwrappedString, prefix, postfix);

		    // Update selected text with unwrapped version, update set selection
		document.replaceString(offsetSelectionStart, offsetSelectionEnd, unwrappedString);
		selectionModel.setSelection(offsetSelectionStart, offsetSelectionStart + unwrappedString.length());
	}

	/**
	 * Unwrap selection over multiple lines with given prefix and postfix, do given transformations on selection
	 *
	 * @param prefix
	 * @param postfix
	 */
	private void unwrapMultiLineSelection(String prefix, String postfix) {
		    // Unwrap each line, begin/end at selection offsets
		lineNumberSelectionEnd = document.getLineNumber(selectionModel.getSelectionEnd());

		for (int lineNumber = lineNumberSelectionEnd; lineNumber >= lineNumberSelectionStart; lineNumber--) {
			int offsetLineStart = document.getLineStartOffset(lineNumber);
			String lineText = UtilsTextual.extractLine(document, lineNumber);
			int offsetLineEnd = offsetLineStart + lineText.length() - 1;

			lineText = lineText.replaceAll("\n", "");
			lineText = UtilsTextual.unwrap(lineText, prefix, postfix);

			document.replaceString(offsetLineStart, offsetLineEnd, lineText);
		}

		    // Update selection: all lines of selection fully
		selectionModel.setSelection(document.getLineStartOffset(lineNumberSelectionStart), document.getLineEndOffset(lineNumberSelectionEnd));
	}

}
