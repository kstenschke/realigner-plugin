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
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogSplitOptions;
import com.kstenschke.realigner.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

public class Splitter {

    final private Project project;
    private final Editor editor;

    /**
     * Constructor
     *
     * @param editor
     */
    public Splitter(Project project, Editor editor) {
        this.project = project;
        this.editor  = editor;
    }

    /**
     * Split line at soft-wrap (no selection + empty delimiter)
     *
     * @param document
     */
    public void splitLineAtSoftWrap(Document document) {
        int caretOffset = editor.getCaretModel().getOffset();
        int lineNumber = document.getLineNumber(caretOffset);
        int offsetLineStart = document.getLineStartOffset(lineNumber);

        String lineText = UtilsTextual.extractLine(document, lineNumber);
        Integer textLength = getTextWidth(lineText);

        if (null == textLength || textLength < 121) {
            return;
        }

        int offsetLineEnd = offsetLineStart + lineText.length() - 1;
        int wrapPosition = 120;
        String wrapChar = lineText.substring(wrapPosition, wrapPosition + 1);

        while (wrapPosition > 0 && isUnsplittableChar(wrapChar)) {
            wrapPosition--;
            wrapChar = lineText.substring(wrapPosition, wrapPosition + 1);
        }
        if (wrapPosition <= 1) {
            return;
        }

        String explodedText = lineText.substring(0, wrapPosition) +
                (wrapChar.equals(",") ? wrapChar : "") + "\n" +
                lineText.substring(wrapPosition + 1);

        ApplicationManager.getApplication().runWriteAction(() -> {
            document.replaceString(offsetLineStart, offsetLineEnd, explodedText);

            editor.getCaretModel().moveToOffset(document.getLineStartOffset(lineNumber + 1));
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        });
    }

    /**
     * Explode line containing the caret by delimiter
     *
     * @param document
     * @param delimiter
     * @param trimWhitespace
     * @param delimiterDisposalMethod
     */
    public void splitLine(Document document, SelectionModel selectionModel, String delimiter, boolean trimWhitespace, Integer delimiterDisposalMethod) {
        int caretOffset = editor.getCaretModel().getOffset();
        int lineNumber = document.getLineNumber(caretOffset);

        int offsetLineStart = document.getLineStartOffset(lineNumber);
        String lineText = UtilsTextual.extractLine(document, lineNumber);
        int offsetLineEnd = offsetLineStart + lineText.length() - 1;

        if (!lineText.contains(delimiter)) {
            JOptionPane.showMessageDialog(null, StaticTexts.NOTIFICATION_SPLIT_DELIMITER_MISSING);
        } else {
            ApplicationManager.getApplication().runWriteAction(() -> {
                document.replaceString(offsetLineStart, offsetLineEnd, getExplodedLineText(delimiter, trimWhitespace, delimiterDisposalMethod, lineText));
                alignSelectedLinesIndent(document, selectionModel);
            });
        }
    }

    public void splitSelection(Document document, SelectionModel selectionModel, String delimiter, boolean trimWhitespace, Integer delimiterDisposalMethod) {
        int offsetStart = selectionModel.getSelectionStart();
        int offsetEnd = selectionModel.getSelectionEnd();

        CharSequence editorText = document.getCharsSequence();
        String selectedText = UtilsTextual.getSubString(editorText, offsetStart, offsetEnd);

        if (null != selectedText && !selectedText.contains(delimiter)) {
            JOptionPane.showMessageDialog(null, StaticTexts.NOTIFICATION_SPLIT_DELIMITER_MISSING);
        } else {
            ApplicationManager.getApplication().runWriteAction(() -> {
                int selectionStart = document.getLineNumber(selectionModel.getSelectionStart());
                int selectionEnd = document.getLineNumber(selectionModel.getSelectionEnd());

                for (int lineNumber = selectionEnd; lineNumber >= selectionStart; lineNumber--) {
                    int offsetLineStart = document.getLineStartOffset(lineNumber);
                    String lineText = UtilsTextual.extractLine(document, lineNumber);
                    int offsetLineEnd = offsetLineStart + lineText.length() - 1;

                    String exploded = getExplodedLineText(delimiter, trimWhitespace, delimiterDisposalMethod, lineText);

                    document.replaceString(offsetLineStart, offsetLineEnd, exploded);
                }
                alignSelectedLinesIndent(document, selectionModel);
            });
        }
    }

    private void alignSelectedLinesIndent(Document document, SelectionModel selectionModel) {
        // Fetch leading whitespace of first line
        int offsetSelectionStart = selectionModel.getSelectionStart();
        int lineNumberSelectionStart = document.getLineNumber(offsetSelectionStart);
        int offsetSelectionEnd = selectionModel.getSelectionEnd();

        int startOffsetFirstLine = document.getLineStartOffset(lineNumberSelectionStart);
        int endOffsetFirstLine = document.getLineEndOffset(lineNumberSelectionStart);
        String firstLine = document.getText(new TextRange(startOffsetFirstLine, endOffsetFirstLine));
        String indent = firstLine.replace(UtilsTextual.lTrim(firstLine), "");

        String[] selectedLines = document.getText(new TextRange(offsetSelectionStart, offsetSelectionEnd)).split("\n");
        int index = 0;
        for (String line : selectedLines) {
            if (index > 0 && !line.startsWith(indent)) {
                selectedLines[index] = indent + line;
            }
            index++;
        }
        document.replaceString(offsetSelectionStart, offsetSelectionEnd, StringUtils.join(selectedLines, "\n"));
    }

    private String getExplodedLineText(String delimiter, boolean trimWhitespace, Integer delimiterDisposalMethod, String lineText) {
        String replacement = getSplitReplacementByDelimiterDisposalMethod(delimiter, delimiterDisposalMethod);
        String result      = lineText.replace(delimiter, replacement);

        return trimWhitespace ? UtilsTextual.trimLines(result) : result;
    }

    /**
     * @param   charStr
     * @return  Does the given character not allow splitting the line at?
     */
    private static boolean isUnsplittableChar(String charStr) {
        return charStr.equals(" ") || charStr.equals("\t") || charStr.equals(",");
    }

    /**
     * Get visual length of given text, that is in editor where tabs resolve to the width of multiple characters
     *
     * @param   text        Text to be "measured"
     * @return  Integer     Amount of characters
     */
    private Integer getTextWidth(String text) {
        if (null == this.project) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(this.project).getPsiFile(this.editor.getDocument());
        if (null != psiFile) {
            return null;
        }

        CommonCodeStyleSettings commonCodeStyleSettings = new CommonCodeStyleSettings(psiFile.getLanguage());
        CommonCodeStyleSettings.IndentOptions indentOptions = commonCodeStyleSettings.getIndentOptions();

        // Get tab size
        int tabSize = null != indentOptions ? indentOptions.TAB_SIZE : 0;
        if (0 == tabSize) {
            tabSize = this.editor.getSettings().getTabSize(this.project);
        }

        return text.length() + UtilsTextual.countTabOccurrences(text) * (tabSize - 1);
    }

    /**
     * Get split replacement string, according to given delimiter and delimiter disposal method
     *
     * @param   delimiter      Delimiter string
     * @param   disposalMethod   Before/At/After
     * @return  String
     */
    private String getSplitReplacementByDelimiterDisposalMethod(String delimiter, Integer disposalMethod) {
        if (DialogSplitOptions.METHOD_DELIMITER_DISPOSAL_BEFORE == disposalMethod) {
            return "\n" + delimiter;
        }
        if (DialogSplitOptions.METHOD_DELIMITER_DISPOSAL_AFTER == disposalMethod) {
            return delimiter + "\n";
        }

        return "\n";
    }
}
