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
import com.kstenschke.realigner.StaticTexts;
import com.kstenschke.realigner.UtilsEnvironment;
import com.kstenschke.realigner.UtilsTextual;
import com.kstenschke.realigner.resources.forms.DialogSplitOptions;

import javax.swing.*;
import java.awt.*;


/**
 * Implode / Explode Action
 */
public class SplitAction extends AnAction {

    Editor  editor;
    Project project;

	/**
	 * Disable when no project open
	 *
	 * @param event Action system event
	 */
	public void update(AnActionEvent event) {
        this.editor = event.getData(PlatformDataKeys.EDITOR);

		event.getPresentation().setEnabled(this.editor != null);
	}

	/**
	 * Perform split into lines
	 *
	 * @param   event   Action system event
	 */
	public void actionPerformed(final AnActionEvent event) {
		this.project = event.getData(PlatformDataKeys.PROJECT);

		CommandProcessor.getInstance().executeCommand(project, new Runnable() {
			public void run() {
				ApplicationManager.getApplication().runWriteAction(new Runnable() {
					public void run() {
						if (editor != null) {
							final Document document = editor.getDocument();
							SelectionModel selectionModel = editor.getSelectionModel();
							boolean hasSelection = selectionModel.hasSelection();

							    // Setup and display options dialog
							DialogSplitOptions splitOptionsDialogDialog = showOptionsDialog();

							    // Clicked ok: conduct split
							if (splitOptionsDialogDialog.clickedOk) {
								String delimiter = splitOptionsDialogDialog.getDelimiter();
								Integer delimiterDisposalMethod = splitOptionsDialogDialog.getDelimiterDisposalMethod();

								Preferences.saveSplitProperties(delimiter, delimiterDisposalMethod);

								if (delimiter != null && delimiter.length() > 0) {
									if (hasSelection) {
                                        splitSelection(document, selectionModel, delimiter, delimiterDisposalMethod);
									} else {
                                        splitLine(editor, document, delimiter, delimiterDisposalMethod);
                                    }
								} else if (!hasSelection) {
                                    splitLineAtSoftWrap(editor, document);
                                }
							}
						}
					}

                    /**
                     * Split line at soft-wrap (no selection + empty delimiter)
                     *
                     * @param   editor
                     * @param   document
                     */
                    private void splitLineAtSoftWrap(Editor editor, Document document) {
                        int caretOffset     = editor.getCaretModel().getOffset();
                        int lineNumber      = document.getLineNumber(caretOffset);
                        int offsetLineStart = document.getLineStartOffset(lineNumber);
                        String lineText     = UtilsTextual.extractLine(document, lineNumber);
                        Integer textLength  = getTextWidth(lineText, editor);

                        if (textLength > 120) {
                            int offsetLineEnd   = offsetLineStart + lineText.length() - 1;
                            int wrapPosition    = 120;
                            String wrapChar     = lineText.substring(wrapPosition, wrapPosition + 1);

                            while (wrapPosition > 0 && !isSplittableChar(wrapChar)) {
                                wrapPosition--;
                                wrapChar = lineText.substring(wrapPosition, wrapPosition + 1);
                            }
                            if (wrapPosition > 1) {
                                String explodedText = lineText.substring(0, wrapPosition) +
                                          (wrapChar.equals(",") ? wrapChar : "") +  "\n" +
                                          lineText.substring(wrapPosition + 1, lineText.length());

                                document.replaceString(offsetLineStart, offsetLineEnd, explodedText);

                                editor.getCaretModel().moveToOffset(document.getLineStartOffset(lineNumber + 1));
                                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                            }
                        }
                    }

                    /**
                     * @param   editor
                     * @param   document
                     * @param   delimiter
                     * @param   delimiterDisposalMethod
                     */
                    private void splitLine(Editor editor, Document document, String delimiter, Integer delimiterDisposalMethod) {
                            // Explode line containing the caret by delimiter
                        int caretOffset = editor.getCaretModel().getOffset();
                        int lineNumber  = document.getLineNumber(caretOffset);

                        int offsetLineStart = document.getLineStartOffset(lineNumber);
                        String lineText     = UtilsTextual.extractLine(document, lineNumber);
                        int offsetLineEnd   = offsetLineStart + lineText.length() - 1;

                        if (!lineText.contains(delimiter)) {
                            JOptionPane.showMessageDialog(null, StaticTexts.NOTIFICATION_SPLIT_DELIMITER_MISSING);
                        } else {
                            document.replaceString(offsetLineStart, offsetLineEnd, getExplodedLineText(delimiter, delimiterDisposalMethod, lineText));
                        }
                    }

                    /**
                     * @param   document
                     * @param   selectionModel
                     * @param   delimiter
                     * @param   delimiterDisposalMethod
                     */
                    private void splitSelection(Document document, SelectionModel selectionModel, String delimiter, Integer delimiterDisposalMethod) {
                        int offsetStart = selectionModel.getSelectionStart();
                        int offsetEnd = selectionModel.getSelectionEnd();

                            // Explode all selected lines by delimiter
                        CharSequence editorText = document.getCharsSequence();
                        String selectedText = UtilsTextual.getSubString(editorText, offsetStart, offsetEnd);

                        if (!selectedText.contains(delimiter)) {
                            JOptionPane.showMessageDialog(null, StaticTexts.NOTIFICATION_SPLIT_DELIMITER_MISSING);
                        } else {
                            int lineStart   = document.getLineNumber(selectionModel.getSelectionStart());
                            int lineEnd     = document.getLineNumber(selectionModel.getSelectionEnd());

                            for (int lineNumber = lineEnd; lineNumber >= lineStart; lineNumber--) {
                                int offsetLineStart = document.getLineStartOffset(lineNumber);
                                String lineText     = UtilsTextual.extractLine(document, lineNumber);
                                int offsetLineEnd   = offsetLineStart + lineText.length() - 1;

                                document.replaceString(offsetLineStart, offsetLineEnd, getExplodedLineText(delimiter, delimiterDisposalMethod, lineText));
                            }
                        }
                    }

                    /**
                     * @param   delimiter
                     * @param   delimiterDisposalMethod
                     * @param   lineText
                     * @return  String
                     */
                    private String getExplodedLineText(String delimiter, Integer delimiterDisposalMethod, String lineText) {
						String replacement = getSplitReplacementByDelimiterDisposalMethod(delimiter, delimiterDisposalMethod);
						return lineText.replace(delimiter, replacement);
					}
				});

			}
		}, StaticTexts.UNDO_HISTORY_SPLIT, UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);
	}

	/**
	 * @param   charStr
	 * @return  Does the given character allow splitting the line at?
	 */
	private static Boolean isSplittableChar(String charStr) {
		return charStr.equals(" ") || charStr.equals("\t") || charStr.equals(",");
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
			tabSize = indentOptions.TAB_SIZE;
		}
		if (tabSize == 0) {
			tabSize = editor.getSettings().getTabSize(editor.getProject());
		}

		Integer amountTabs = UtilsTextual.countSubstringOccurrences(text, "\t");

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
		if (disposalMethod == DialogSplitOptions.METHOD_DELIMITER_DISPOSAL_BEFORE) {
			return "\n" + delimiter;
		} else if (disposalMethod == DialogSplitOptions.METHOD_DELIMITER_DISPOSAL_AFTER) {
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
		DialogSplitOptions splitOptionsDialogDialog = new DialogSplitOptions();

            // Load and init dialog options from preferences
        splitOptionsDialogDialog.setDelimiter(Preferences.getSplitDelimiter());
        splitOptionsDialogDialog.setDelimiterDisposalMethod(Integer.parseInt(Preferences.getSplitWhere()));

        UtilsEnvironment.setDialogVisible(editor, splitOptionsDialogDialog, StaticTexts.MESSAGE_TITLE_SPLIT);

		return splitOptionsDialogDialog;
	}

}
