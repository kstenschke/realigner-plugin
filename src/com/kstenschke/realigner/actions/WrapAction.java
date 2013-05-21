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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.kstenschke.realigner.Preferences;
import com.kstenschke.realigner.resources.forms.WrapOptions;


/**
 * Wrap Action
 */
public class WrapAction extends AnAction {

	/**
	 * Disable when no project open
	 *
	 * @param   event   Action system event
	 */
	public void update(AnActionEvent event) {
		event.getPresentation().setEnabled(event.getData(PlatformDataKeys.EDITOR) != null);
	}

	/**
	 * Perform wrap or unwrap
	 * Show options dialog, than wrap/unwrap current selection or line of caret or each of the selected lines
	 *
	 * @param   event   Action system event
	 */
	public void actionPerformed(final AnActionEvent event) {
		final Project currentProject = event.getData(PlatformDataKeys.PROJECT);

		ApplicationManager.getApplication().runWriteAction(new Runnable() {
			public void run() {
				Editor editor = event.getData(PlatformDataKeys.EDITOR);

				if (editor != null) {
					final Wrapper wrapper = new Wrapper(editor);
					WrapOptions wrapOptionsDialog = wrapper.showWrapOptions();

					final String prefix = wrapOptionsDialog.getTextFieldPrefix();
					final String postfix = wrapOptionsDialog.getTextFieldPostfix();

					// Store preferences
					Preferences.saveWrapProperties(prefix, postfix);

					// Perform actual wrap or unwrap
					if (wrapOptionsDialog.clickedOperation == WrapOptions.OPERATION_WRAP) {
						CommandProcessor.getInstance().executeCommand(currentProject, new Runnable() {
							public void run() {
								wrapper.wrap(prefix, postfix);
							}
						}, "Wrap", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);

					} else if (wrapOptionsDialog.clickedOperation == WrapOptions.OPERATION_UNWRAP) {
						CommandProcessor.getInstance().executeCommand(currentProject, new Runnable() {
							public void run() {
								wrapper.unwrap(prefix, postfix);
							}
						}, "Unwrap", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);

					}
				}
			}
		});

	}

}
