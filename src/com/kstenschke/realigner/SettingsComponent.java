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

package com.kstenschke.realigner;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.kstenschke.realigner.resources.forms.PluginConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class SettingsComponent implements ProjectComponent, Configurable {

	private PluginConfiguration form;
	private ImageIcon icon;
	private Settings settings;

	public JComponent createComponent() {
		if (form == null) {
			form = new PluginConfiguration();
		}

		return form.getRootPanel();
	}

	@Nls
	public String getDisplayName() {
		return "Realigner (Quick Wrap Buttons)";
	}

	public boolean isModified() {
		return form != null && form.isModified(settings);
	}

	public void disposeUIResources() {
		form = null;
	}

	public void reset() {
		if (form != null) {
				// Reset form data from component
			form.setData(settings);
		}
	}

	public Icon getIcon() {
		return icon;
	}

	public void apply() throws ConfigurationException {
		if (form != null) {
				// Get data from form to component
			form.getData(settings);
			applyGlobalSettings();
		}
	}

	public String getHelpTopic() {
		return null;
	}

	private void applyGlobalSettings() {

	}






	public SettingsComponent(Project project) {
	}

	public void initComponent() {
		// TODO: insert component initialization logic here
	}

	public void disposeComponent() {
		// TODO: insert component disposal logic here
	}

	@NotNull
	public String getComponentName() {
		return "SettingsComponent";
	}

	public void projectOpened() {
		// called when project is opened
	}

	public void projectClosed() {
		// called when project is being closed
	}
}
