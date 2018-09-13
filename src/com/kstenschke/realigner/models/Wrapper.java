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
package com.kstenschke.realigner.models;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.kstenschke.realigner.*;
import com.kstenschke.realigner.resources.forms.DialogWrapOptions;
import com.kstenschke.realigner.utils.UtilsTextual;

public class Wrapper {

    private final Editor editor;
    private final Document document;

    private SelectionModel selectionModel;
    private boolean hasSelection = false;
    public boolean isSelectionMultiLine = false;
    private int offsetSelectionStart;
    private int offsetSelectionEnd;
    private int lineNumberSelectionStart;
    private int lineNumberSelectionEnd;
    private CaretModel caretModel;

    /**
     * Constructor
     *
     * @param editor The editor
     */
    public Wrapper(Editor editor) {
        this.editor = editor;
        this.document = editor.getDocument();

        this.initSelectionProperties();
        this.initCaretProperties();
    }

    /**
     * Initialize selection related wrapper properties
     */
    private void initSelectionProperties() {
        this.selectionModel = editor.getSelectionModel();

        this.hasSelection = selectionModel.hasSelection();

        if (this.hasSelection) {
            this.offsetSelectionStart   = this.selectionModel.getSelectionStart();
            this.offsetSelectionEnd     = this.selectionModel.getSelectionEnd();

            this.lineNumberSelectionStart   = this.document.getLineNumber(this.offsetSelectionStart);
            this.lineNumberSelectionEnd     = this.document.getLineNumber(this.offsetSelectionEnd);

            this.isSelectionMultiLine = lineNumberSelectionStart < lineNumberSelectionEnd;
        }
    }

    private void initCaretProperties() {
        this.caretModel = this.editor.getCaretModel();
    }

    /**
     * @return  int     Line number where the caret is
     */
    private int getCaretLineNumber() {
        int caretOffset = editor.getCaretModel().getOffset();

        return document.getLineNumber(caretOffset);
    }

    /**
     * @return  String  Selected text
     */
    private String getSelectedText() {
        CharSequence editorText = document.getCharsSequence();

        return UtilsTextual.getSubString(editorText, this.offsetSelectionStart, this.offsetSelectionEnd);
    }

    /**
     * Perform wrapping of line around caret, single- or multi-line selection
     *
     * @param   prefix
     * @param   postfix
     * @param   wrapMode    If multi-line: wrap each line / whole selection
     */
    public void wrap(final String prefix, final String postfix, final Integer wrapMode) {
        ApplicationManager.getApplication().runWriteAction(() -> this.caretModel.runForEachCaret(caret -> {
            if (hasSelection) {
                // get selection offsets and line numbers
                updateCaretSelectionProperties(caret);

                if (document.getLineStartOffset(lineNumberSelectionEnd) == offsetSelectionEnd) {
                    lineNumberSelectionEnd--;
                }

                if (lineNumberSelectionStart == lineNumberSelectionEnd || wrapMode.equals(DialogWrapOptions.MODE_WRAP_WHOLE)) {
                    String selectedText = getSelectedText();
                    wrapSingleLinedSelection(selectedText, prefix, postfix);
                } else {
                    wrapMultiLineSelection(prefix, postfix);
                }
            } else {
                // No selection: wrap the line where the caret is
                wrapCaretLine(prefix, postfix);
            }
        }));
    }

    private void updateCaretSelectionProperties(Caret caret) {
        offsetSelectionStart = caret.getSelectionStart();
        offsetSelectionEnd = caret.getSelectionEnd();

        lineNumberSelectionStart = document.getLineNumber(offsetSelectionStart);
        lineNumberSelectionEnd = document.getLineNumber(offsetSelectionEnd);
    }

    /**
     * Perform unwrapping of line around caret, single- or multi-line selection
     *
     * @param   prefix
     * @param   postfix
     */
    public void unwrap(final String prefix, final String postfix) {
        ApplicationManager.getApplication().runWriteAction(() -> this.caretModel.runForEachCaret(caret -> {
            updateCaretSelectionProperties(caret);

            if (hasSelection) {
                if (document.getLineStartOffset(lineNumberSelectionEnd) == offsetSelectionEnd) {
                    lineNumberSelectionEnd--;
                }

                if (lineNumberSelectionStart == lineNumberSelectionEnd) {
                    unwrapSingleLinedSelection(prefix, postfix);
                } else {
                    unwrapMultiLineSelection(prefix, postfix);
                }
            } else {
                // No selection: wrap the line where the caret is
                unwrapCaretLine(prefix, postfix);
            }
        }));
    }

    /**
     * Wrap selection over multiple lines with given prefix and postfix, do given transformations on selection
     *
     * @param   prefix
     * @param   postfix
     */
    private void wrapMultiLineSelection(String prefix, String postfix) {
        // Wrap each line, begin/end at selection offsets
        int prefixLen = prefix.length();
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
        selectionModel.setSelection(
            document.getLineStartOffset(lineNumberSelectionStart),
            document.getLineEndOffset(lineNumberSelectionEnd)
        );
    }

    /**
     * Unwrap selection within a single line with given prefix and postfix, do given transformations on selection
     *
     * @param   prefix
     * @param   postfix
     */
    private void wrapSingleLinedSelection(String selectedText, String prefix, String postfix) {
        String wrappedString = prefix + selectedText + postfix;
        document.replaceString(offsetSelectionStart, offsetSelectionEnd, wrappedString);

        // Update selection
        selectionModel.setSelection(offsetSelectionStart, offsetSelectionStart + wrappedString.length());
    }

    /**
     * Wrap the line where the caret is with given prefix and postfix, do given transformations on the line
     *
     * @param   prefix
     * @param   postfix
     */
    private void wrapCaretLine(String prefix, String postfix) {
        int lineNumber      = getCaretLineNumber();
        int offsetLineStart = document.getLineStartOffset(lineNumber);
        String lineText     = UtilsTextual.extractLine(document, lineNumber);
        int offsetLineEnd   = offsetLineStart + lineText.length() - 1;

        document.insertString(offsetLineEnd, postfix);
        document.insertString(offsetLineStart, prefix);

        lineText = lineText.replaceAll("\n", "");
        int prefixLen = prefix.length();

        document.replaceString(offsetLineStart + prefixLen, offsetLineEnd + prefixLen, lineText);

        // Update selection: whole line
        selectionModel.setSelection(document.getLineStartOffset(lineNumber), document.getLineEndOffset(lineNumber));
    }

    /**
     * Unwrap the line the caret is in
     *
     * @param   prefix
     * @param   postfix
     */
    private void unwrapCaretLine(String prefix, String postfix) {
        int lineNumber      = getCaretLineNumber();
        int offsetLineStart = document.getLineStartOffset(lineNumber);
        String lineText     = UtilsTextual.extractLine(document, lineNumber);
        int offsetLineEnd   = offsetLineStart + lineText.length() - 1;

        this.offsetSelectionStart   = offsetLineStart;
        this.offsetSelectionEnd     = offsetLineEnd;

        this.unwrapSingleLinedSelection(prefix, postfix);
    }

    /**
     * Remove given strings from the beginning and ending of current selection
     *
     * @param   prefix
     * @param   postfix
     */
    private void unwrapSingleLinedSelection(String prefix, String postfix) {
        CharSequence editorText = document.getCharsSequence();
        String unwrappedString  = UtilsTextual.getSubString(editorText, offsetSelectionStart, offsetSelectionEnd);

        if (null == unwrappedString) {
            return;
        }
        unwrappedString = UtilsTextual.unwrap(unwrappedString, prefix, postfix);

        // Update selected text with unwrapped version, update set selection
        document.replaceString(offsetSelectionStart, offsetSelectionEnd, unwrappedString);
        selectionModel.setSelection(offsetSelectionStart, offsetSelectionStart + unwrappedString.length());
    }

    /**
     * Unwrap selection over multiple lines with given prefix and postfix, do given transformations on selection
     *
     * @param   prefix
     * @param   postfix
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
        selectionModel.setSelection(
            document.getLineStartOffset(lineNumberSelectionStart),
            document.getLineEndOffset(lineNumberSelectionEnd)
        );
    }

    /**
     * @param   prefix
     * @param   postfix
     * @return  boolean     Is caret line or selection wrapped into given pre/postfix?
     */
    public boolean isWrapped(String prefix, String postfix) {
        String text = hasSelection
                ? document.getText().substring(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd() )
                : UtilsTextual.extractLine(document, getCaretLineNumber() );
        text    = text.trim();

        if (Preferences.getMultiLineWrapMode() != DialogWrapOptions.MODE_WRAP_WHOLE && text.contains("\n")) {
            // Wrap mode works on every line: Analyze first line only
            text = text.split("\n")[0].trim();
        }

        return text.startsWith(prefix) && text.endsWith(postfix);
    }
}
