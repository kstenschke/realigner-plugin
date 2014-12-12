/*
 * Copyright 2012-2014 Kay Stenschke
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

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;

public class SettingsQuickWraps {

	    // Wrap button items are stored custom serialized: each item as a config string
	@NonNls
	private static final String PROPERTY_WRAPBUTTONS = "";

	/**
	 * Store given wrap button config into wrap buttons store
	 *
	 * @param   label     Item label
	 * @param   prefix          Wrap LHS string
	 * @param   postfix         Wrap RHS string
	 */
	private static void saveButton(String label, String prefix, String postfix, Boolean saveTopMost) {
		if (!label.isEmpty()) {
			// Delete pre-existing button config with same label, if stored already
			removeWrapButton(label);

			// Get new button config, stored button items
			String newButtonConfigStr = renderButtonConfigStr(label, prefix, postfix);
			String storeWrapButtons = loadButtonItemsConfig();

			if (storeWrapButtons == null) {
				storeWrapButtons = "";
			}

			// Store button config
			storeWrapButtons = saveTopMost
                    ? newButtonConfigStr.concat(storeWrapButtons)
                    : storeWrapButtons.concat(newButtonConfigStr);
			saveButtonItemsConfig(storeWrapButtons);
		}
	}

    public static void saveButton(String label, String prefix, String postfix) {
        saveButton(label, prefix, postfix, false);
    }

    public static void makeButtonTopMost(String buttonLabel, String prefix, String postfix) {
        removeWrapButton(buttonLabel);
        saveButton(buttonLabel, prefix, postfix, true);
    }

	/**
	 * Store given wrap buttons' items config
	 *
	 * @param   itemsConfig      Custom serialized items config string
	 */
	private static void saveButtonItemsConfig(String itemsConfig) {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
		propertiesComponent.setValue(PROPERTY_WRAPBUTTONS, itemsConfig);
	}

	/**
	 * @return  String      Wrap buttons setting
	 */
	private static String loadButtonItemsConfig() {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		return propertiesComponent.getValue(PROPERTY_WRAPBUTTONS);
	}

	/**
	 * Find and remove button item with given label from store
	 *
	 * @param   label     Label of button to be removed
	 */
	public static void removeWrapButton(String label) {
		Object[] buttonLabels = getAllButtonLabels();
		if (buttonLabels != null) {
			Integer deleteButtonIndex = null;
			for (int i = 0; i < buttonLabels.length; i++) {
				if (buttonLabels[i].equals(label)) {
					deleteButtonIndex = i;
				}
			}

			if (deleteButtonIndex != null) {
				// Remove button config with found index
				String storeItemsConfig = loadButtonItemsConfig();

				String buttonsConfigWithoutDeletedButton = "";
				String[] buttonsConfigs = storeItemsConfig.split("##WBUTTON####WBLABEL##");
				for (int i = 1; i < buttonsConfigs.length; i++) {
					if (i != (deleteButtonIndex + 1)) {
						buttonsConfigWithoutDeletedButton = buttonsConfigWithoutDeletedButton.concat("##WBUTTON####WBLABEL##" + buttonsConfigs[i]);
					}
				}
				saveButtonItemsConfig(buttonsConfigWithoutDeletedButton);
			}
		}
	}

	/**
	 * @param   label     Item label
	 * @param   prefix          Wrap LHS string
	 * @param   postfix         Wrap RHS string
	 * @return  String          configuration string for given wrap button options
	 */
	private static String renderButtonConfigStr(String label, String prefix, String postfix) {
		return  "##WBUTTON##"
          + "##WBLABEL##" + label + "##/WBLABEL##"
          + "##WBPREFIX##" + prefix + "##/WBPREFIX##"
          + "##WBPOSTFIX##" + postfix + "##/WBPOSTFIX##"
          + "##/WBUTTON##";
	}

	/**
	 * @return  Object[]    Array of stored buttons' labels
	 */
	public static Object[] getAllButtonLabels() {
		return getAllButtonAttributesByType("WBLABEL");
	}

	/**
	 * @return  Object[]    Array of stored buttons' prefix values
	 */
	public static Object[] getAllButtonPrefixes() {
		return getAllButtonAttributesByType("WBPREFIX");
	}

	/**
	 * @return  Object[]    Array of stored buttons' postfix values
	 */
	public static Object[] getAllButtonPostfixes() {
		return getAllButtonAttributesByType("WBPOSTFIX");
	}

	/**
	 * @param   typeName    String e.g. "WBPREFIX"
	 * @return  Object[]    Array of stored buttons' prefix values
	 */
	private static Object[] getAllButtonAttributesByType(String typeName) {
		typeName = typeName.trim().toUpperCase();

		String storeItemsConfig = loadButtonItemsConfig();

		// Extract only item prefixes into array
		List<String> items = new ArrayList<String>();
		if (storeItemsConfig != null && !storeItemsConfig.isEmpty()) {
			String[] buttonConfigs = storeItemsConfig.split("##" + typeName + "##");

			for (int i = 1; i < buttonConfigs.length; i++) {
				items.add(buttonConfigs[i].split("##/" + typeName + "##")[0]);
			}
		}

		return items.toArray();
	}

	/**
	 * @return  Integer     Amount of stored wrap buttons
	 */
	private static Integer getAmountButtons() {
		return getAllButtonLabels().length;
	}

	/**
	 * @return  boolean     Are any wrap buttons configured?
	 */
	public static boolean areAnyButtonsConfigured() {
		return getAmountButtons() > 0;
	}

}