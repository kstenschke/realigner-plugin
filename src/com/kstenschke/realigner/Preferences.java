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

/**
 * Utility functions for preferences handling
 * All preferences of the Realigner plugin are stored on application level (not per project)
 */
public class Preferences {

	@NonNls
	private static final String PROPERTY_JOIN_GLUE = "PluginRealiginer.JoinGlue";
	@NonNls
	private static final String PROPERTY_WRAP_PREFIX = "PluginRealiginer.WrapPrefix";
	@NonNls
	private static final String PROPERTY_WRAP_POSTFIX = "PluginRealiginer.WrapPostfix";
	@NonNls
	private static final String PROPERTY_SPLIT_DELIMITER = "PluginRealiginer.SplitDelimiter";
    @NonNls
	private static final String PROPERTY_SPLIT_TRIM_WHITESPACE = "PluginRealiginer.SplitTrimWhitespace";
	@NonNls
	private static final String PROPERTY_SPLIT_WHERE = "PluginRealiginer.SplitSplitWhere";

        // Wrap modes
    @NonNls
    private static final String PROPERTY_MODE_WRAP_QUICK = "PluginRealiginer.QuickWrapMode";
    @NonNls
    private static final String PROPERTY_MODE_WRAP_MULTILINE = "PluginRealiginer.MultiLineWrapMode";

	/**
	 * Store wrap preferences
	 *
	 * @param   prefix      Wrap prefix string (LHS)
	 * @param   postfix      Wrap postfix string (RHS)
	 */
	public static void saveWrapProperties(String prefix, String postfix) {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		propertiesComponent.setValue(PROPERTY_WRAP_PREFIX, prefix);
		propertiesComponent.setValue(PROPERTY_WRAP_POSTFIX, postfix);
	}

    /**
     * @param   mode    Un/wrap
     */
    public static void saveMultiLineWrapMode(Integer mode) {
        PropertiesComponent.getInstance().setValue(PROPERTY_MODE_WRAP_MULTILINE, mode.toString());
    }

    /**
     * @param   mode    Un/wrap
     */
    public static void saveQuickWrapMode(Integer mode) {
        PropertiesComponent.getInstance().setValue(PROPERTY_MODE_WRAP_QUICK, mode.toString());
    }

	/**
	 * Store split preferences
	 *
	 * @param   delimiter                   Delimiter string
	 * @param   trimWhitespace              Trim items whitespace?
	 * @param   delimiterDisposalMethod     Split at/after/before?
	 */
	public static void saveSplitProperties(String delimiter, Boolean trimWhitespace, Integer delimiterDisposalMethod) {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		propertiesComponent.setValue(PROPERTY_SPLIT_DELIMITER, delimiter);
        propertiesComponent.setValue(PROPERTY_SPLIT_TRIM_WHITESPACE, trimWhitespace ? "1":"0");

		if (delimiterDisposalMethod > 2) delimiterDisposalMethod = 0;
		propertiesComponent.setValue(PROPERTY_SPLIT_WHERE, delimiterDisposalMethod.toString());
	}

	/**
	 * Store wrap preferences
	 *
	 * @param   glue   Join glue string
	 */
	public static void saveJoinProperties(String glue) {
		PropertiesComponent.getInstance().setValue(PROPERTY_JOIN_GLUE, glue);
	}

	/**
	 * @param   propertyName        Name of the preference property
	 * @param   defaultValue        Default value to be set if null
	 * @param   setDefaultIfEmpty   Set default also if empty?
	 * @return  String
	 */
	private static String getProperty(String propertyName, String defaultValue, Boolean setDefaultIfEmpty) {
		String value = PropertiesComponent.getInstance().getValue(propertyName);
		if (value == null) {
			value = defaultValue;
		}
		if (value.equals("") && setDefaultIfEmpty && !defaultValue.equals("")) {
			value = defaultValue;
		}

		return value;
	}

    /**
     * @return  Integer QuickWrap Mode
     */
    public static Integer getMultiLineWrapMode() {
        return Integer.parseInt(getProperty(PROPERTY_MODE_WRAP_MULTILINE, "0", true));
    }

    /**
     * @return  Integer QuickWrap Mode
     */
    public static Integer getQuickWrapMode() {
        return Integer.parseInt(getProperty(PROPERTY_MODE_WRAP_QUICK, "1", true));
    }

	/**
	 * @return  String  Split delimiter
	 */
	public static String getSplitDelimiter() {
		return getProperty(PROPERTY_SPLIT_DELIMITER, "", true);
	}

	/**
	 * @return  String  Split delimiter
	 */
	public static Boolean getIsSplitIsSelectedTrimWhitespace() {
		return getProperty(PROPERTY_SPLIT_TRIM_WHITESPACE, "1", true).equals("1");
	}

	/**
	 * Get split option: split at/before/after delimiter
	 *
	 * @return  String  "0" (default if not saved yet) / "1" / "2"
	 */
	public static String getSplitWhere() {
		return getProperty(PROPERTY_SPLIT_WHERE, "0", true);
	}

	/**
	 * @return  String  Join glue preference
	 */
	public static String getJoinGlue() {
		return getProperty(PROPERTY_JOIN_GLUE, "", false);
	}

	/**
	 * @return  String  Wrap prefix
	 */
	public static String getWrapPrefix() {
		return getProperty(PROPERTY_WRAP_PREFIX, ", ", false);
	}

	/**
	 * @return  String  Wrap postfix
	 */
	public static String getWrapPostfix() {
		return getProperty(PROPERTY_WRAP_POSTFIX, ", ", false);
	}

}
