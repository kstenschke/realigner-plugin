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

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;

public class Settings {

		// Wrap button items are stored custom serialized: each item as a config string
	@NonNls
	private static final String PROPERTY_WRAPBUTTONS = "";



	/**
	 * Store given wrap button config into wrap buttons store
	 *
	 * @param buttonLabel
	 * @param prefix
	 * @param postfix
	 * @param escapeSingleQuotes
	 * @param escapeDoubleQuotes
	 * @param escapeBackslashes
	 * @param removeBlankLines
	 */
	public static void addWrapButtonItemToStore(String buttonLabel, String prefix, String postfix, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes, Boolean removeBlankLines) {
		if( !buttonLabel.isEmpty() ) {
				// Get new button config, stored button items
			String newButtonConfigStr	= renderWrapButtonConfigStr(buttonLabel, prefix, postfix, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes, removeBlankLines);
			String storeWrapButtons		= PropertiesComponent.getInstance().getValue(PROPERTY_WRAPBUTTONS);
			if( storeWrapButtons == null ) {
				storeWrapButtons	= "";
			}
				// Add new button config
			storeWrapButtons	= storeWrapButtons.concat(newButtonConfigStr);
			storeWrapButtonItemsConfig(storeWrapButtons);
		}
	}



	/**
	 * Clear stored wrap buttons' items config
	 */
	public static void clearStoredWrapButtonItemsConfig() {
		storeWrapButtonItemsConfig("");
	}



	/**
	 * Store given wrap buttons' items config
	 *
	 * @param itemsConfig
	 */
	private static void storeWrapButtonItemsConfig(String itemsConfig) {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
		propertiesComponent.setValue(PROPERTY_WRAPBUTTONS, itemsConfig);
	}



	/**
	 * Remove wrap button config from wrap button items store
	 *
	 * @param buttonLabel
	 * @param prefix
	 * @param postfix
	 * @param escapeSingleQuotes
	 * @param escapeDoubleQuotes
	 * @param escapeBackslashes
	 * @param removeBlankLines
	 */
	public static void removeWrapButtonItemFromStore(String buttonLabel, String prefix, String postfix, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes, Boolean removeBlankLines) {
		if( !buttonLabel.isEmpty() ) {
				// Get button config, stored button items
			String buttonConfig		= renderWrapButtonConfigStr(buttonLabel, prefix, postfix, escapeSingleQuotes, escapeDoubleQuotes, escapeBackslashes, removeBlankLines);
			String wrapButtonsConfig= PropertiesComponent.getInstance().getValue(PROPERTY_WRAPBUTTONS);
			if( wrapButtonsConfig == null ) {
				wrapButtonsConfig	= "";
			} else {
					// Remove button config
				wrapButtonsConfig	= wrapButtonsConfig.replace(buttonConfig, "");
			}
				// Save to store
			storeWrapButtonItemsConfig(wrapButtonsConfig);
		}
	}



	/**
	 * Find and remove button item with given label from store
	 *
	 * @param buttonLabel
	 */
	public static void removeWrapButtonItemFromStore(String buttonLabel) {
			// Find index of button with given label
		Object[] buttonLabels	= getWrapButtonItemsLabels();
		Integer deleteButtonIndex = null;
		for(int i = 0; i < buttonLabels.length; i++) {
			if( buttonLabels[i].equals(buttonLabel) ) {
				deleteButtonIndex	= i;
			}
		}

		if( deleteButtonIndex != null ) {
				// Remove button config with found index
			PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
			String storeItemsConfig	= propertiesComponent.getValue(PROPERTY_WRAPBUTTONS);

			String	buttonsConfigWithoutDeletedButton	= "";
			String[] buttonsConfigs	= storeItemsConfig.split("##WBUTTON####WBLABEL##");
			for(int i = 1; i < buttonsConfigs.length; i++) {
				if( i != (deleteButtonIndex+1) ) {
					buttonsConfigWithoutDeletedButton	= buttonsConfigWithoutDeletedButton.concat("##WBUTTON####WBLABEL##" + buttonsConfigs[i]);
				}
			}
				// Save
			storeWrapButtonItemsConfig(buttonsConfigWithoutDeletedButton);
		}
	}



	/**
	 * @param	buttonLabel
	 * @param	prefix
	 * @param	postfix
	 * @param	escapeSingleQuotes
	 * @param	escapeDoubleQuotes
	 * @param	escapeBackslashes
	 * @param	removeBlankLines
	 * @return	configuration string for given wrap button options
	 */
	private static String renderWrapButtonConfigStr(String buttonLabel, String prefix, String postfix, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes, Boolean removeBlankLines) {
		String configStr	= "##WBUTTON##";

		configStr	= configStr.concat("##WBLABEL##" + buttonLabel + "##/WBLABEL##");
		configStr	= configStr.concat("##WBPREFIX##" + prefix + "##/WBPREFIX##");
		configStr	= configStr.concat("##WBPOSTFIX##" + postfix + "##/WBPOSTFIX##");
		configStr	= configStr.concat( escapeSingleQuotes ? "1," : "0,");
		configStr	= configStr.concat( escapeDoubleQuotes ? "1," : "0,");
		configStr	= configStr.concat( escapeBackslashes ? "1," : "0,");
		configStr	= configStr.concat( removeBlankLines ? "1" : "0");
		configStr	= configStr.concat("##/WBUTTON##");

		return configStr;
	}



	/**
	 * @return		Array of stored buttons' labels
	 */
	public static Object[] getWrapButtonItemsLabels() {
			// Get items store
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
		String storeItemsConfig	= propertiesComponent.getValue(PROPERTY_WRAPBUTTONS);

			// Extract only item labels into array
		List<String> items = new ArrayList<String>();
		if( storeItemsConfig != null && !storeItemsConfig.isEmpty()) {
			String[] buttonConfigs	= storeItemsConfig.split("##WBLABEL##");

			for( int i = 1; i < buttonConfigs.length; i++) {
				items.add( buttonConfigs[i].split("##/WBLABEL##")[0] );
			}
		}

		return items.toArray();
	}



	/**
	 * @return	Amount of stored wrap buttons
	 */
	public static Integer getAmountWrapButtons() {
		return getWrapButtonItemsLabels().length;
	}



	/**
	 * @return	Are any wrap buttons configured?
	 */
	public static Boolean areWrapButtonsConfigured() {
		return getAmountWrapButtons() > 0;
	}

}