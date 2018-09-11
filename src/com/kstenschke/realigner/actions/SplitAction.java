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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.kstenschke.realigner.*;
import com.kstenschke.realigner.listeners.ComponentListenerDialog;
import com.kstenschke.realigner.resources.StaticTexts;
import com.kstenschke.realigner.resources.forms.DialogSplitOptions;
import com.kstenschke.realigner.utils.UtilsEnvironment;
import com.kstenschke.realigner.utils.UtilsTextual;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Implode / Explode Action
 */
class SplitAction extends AnAction {

    private Editor  editor;
    private Project project;

    /**
     * Disable when no project open
     *
     * @param event Action system event
     */
    public void update(@NotNull AnActionEvent event) {
        this.editor = event.getData(PlatformDataKeys.EDITOR);

        event.getPresentation().setEnabled(this.editor != null);
    }

    /**
     * Perform split into lines
     *
     * @param   event   Action system event
     */
    public void actionPerformed(@NotNull final AnActionEvent event) {
        this.project = event.getData(PlatformDataKeys.PROJECT);

        CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                if (null == editor) {
                    return;
                }

                final Document document         = editor.getDocument();
                SelectionModel selectionModel   = editor.getSelectionModel();
                boolean hasSelection            = selectionModel.hasSelection();

                // Setup and display options dialog
                DialogSplitOptions optionsDialog = showOptionsDialog();

                // Clicked ok: conduct split
                if (!optionsDialog.clickedOk) {
                    return;
                }

                String delimiter       = optionsDialog.getDelimiter();
                boolean trimWhitespace = optionsDialog.getIsSelectedTrimWhitespace();
                Integer delimiterDisposalMethod = optionsDialog.getDelimiterDisposalMethod();

                Preferences.saveSplitProperties(delimiter, trimWhitespace, delimiterDisposalMethod);

                if (delimiter != null && delimiter.length() > 0) {
                    if (hasSelection) {
                        splitSelection(document, selectionModel, delimiter, trimWhitespace, delimiterDisposalMethod);
                    } else {
                        splitLine(document, delimiter, trimWhitespace, delimiterDisposalMethod);
                    }

                    alignSelectedLinesIndent(document, selectionModel);
                } else if (!hasSelection) {
                    splitLineAtSoftWrap(document);
                }
            }

            /**
             * Split line at soft-wrap (no selection + empty delimiter)
             *
             * @param document
             */
            private void splitLineAtSoftWrap(Document document) {
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
                        lineText.substring(wrapPosition + 1, lineText.length());

                document.replaceString(offsetLineStart, offsetLineEnd, explodedText);

                editor.getCaretModel().moveToOffset(document.getLineStartOffset(lineNumber + 1));
                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
            }

            /**
             * Explode line containing the caret by delimiter
             *
             * @param document
             * @param delimiter
             * @param trimWhitespace
             * @param delimiterDisposalMethod
             */
            private void splitLine(Document document, String delimiter, boolean trimWhitespace, Integer delimiterDisposalMethod) {
                int caretOffset = editor.getCaretModel().getOffset();
                int lineNumber = document.getLineNumber(caretOffset);

                int offsetLineStart = document.getLineStartOffset(lineNumber);
                String lineText = UtilsTextual.extractLine(document, lineNumber);
                int offsetLineEnd = offsetLineStart + lineText.length() - 1;

                if (!lineText.contains(delimiter)) {
                    JOptionPane.showMessageDialog(null, StaticTexts.NOTIFICATION_SPLIT_DELIMITER_MISSING);
                } else {
                    document.replaceString(offsetLineStart, offsetLineEnd, getExplodedLineText(delimiter, trimWhitespace, delimiterDisposalMethod, lineText));
                }
            }

            /**
             * @param document
             * @param selectionModel
             * @param delimiter
             * @param trimWhitespace
             * @param delimiterDisposalMethod
             */
            private void splitSelection(Document document, SelectionModel selectionModel, String delimiter, boolean trimWhitespace, Integer delimiterDisposalMethod) {
                int offsetStart = selectionModel.getSelectionStart();
                int offsetEnd = selectionModel.getSelectionEnd();

                CharSequence editorText = document.getCharsSequence();
                String selectedText = UtilsTextual.getSubString(editorText, offsetStart, offsetEnd);

                if (null != selectedText && !selectedText.contains(delimiter)) {
                    JOptionPane.showMessageDialog(null, StaticTexts.NOTIFICATION_SPLIT_DELIMITER_MISSING);
                } else {
                    int selectionStart = document.getLineNumber(selectionModel.getSelectionStart());
                    int selectionEnd   = document.getLineNumber(selectionModel.getSelectionEnd());

                    for (int lineNumber = selectionEnd; lineNumber >= selectionStart; lineNumber--) {
                        int offsetLineStart = document.getLineStartOffset(lineNumber);
                        String lineText = UtilsTextual.extractLine(document, lineNumber);
                        int offsetLineEnd = offsetLineStart + lineText.length() - 1;

                        String exploded = getExplodedLineText(delimiter, trimWhitespace, delimiterDisposalMethod, lineText);

                        document.replaceString(offsetLineStart, offsetLineEnd, exploded);
                    }
                }
            }

            /**
             * @param document
             * @param selectionModel
             */
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

            /**
             * @param delimiter
             * @param delimiterDisposalMethod
             * @param lineText
             * @return String
             */
            private String getExplodedLineText(String delimiter, boolean trimWhitespace, Integer delimiterDisposalMethod, String lineText) {
                String replacement = getSplitReplacementByDelimiterDisposalMethod(delimiter, delimiterDisposalMethod);

                String result = lineText.replace(delimiter, replacement);

                if (trimWhitespace) {
                    result = UtilsTextual.trimLines(result);
                }

                return result;
            }
        }), StaticTexts.UNDO_HISTORY_SPLIT, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
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

    /**
     * Setup and display options dialog for split action
     *
     * @return Split options dialog
     */
    private DialogSplitOptions showOptionsDialog() {
        DialogSplitOptions optionsDialog = new DialogSplitOptions();

        // Load and init dialog options from preferences
        optionsDialog.setDelimiter(Preferences.getSplitDelimiter());
        optionsDialog.setCheckboxTrimWhitespaceSelected(Preferences.getIsSplitIsSelectedTrimWhitespace());
        optionsDialog.setDelimiterDisposalMethod(Integer.parseInt(Preferences.getSplitWhere()));

        optionsDialog.addComponentListener(new ComponentListenerDialog(Preferences.ID_DIALOG_SPLIT));
        UtilsEnvironment.setDialogVisible(editor, Preferences.ID_DIALOG_SPLIT, optionsDialog, StaticTexts.MESSAGE_TITLE_SPLIT);

        return optionsDialog;
    }
}
