package com.kstenschke.realigner.helpers;

import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NonNls;

/**
 * Utility functions for preferences handling
 * All preferences of the Realigner plugin are stored on application level (not per project)
 */
public class Preferences {

		//  @NonNls = element is not a string requiring internationalization and it does not contain such strings.
	@NonNls
	private static final String PROPERTY_JOIN_GLUE = "PluginRealiginer.JoinGlue";

	@NonNls
	private static final String PROPERTY_WRAP_PREFIX = "PluginRealiginer.WrapPrefix";

	@NonNls
	private static final String PROPERTY_WRAP_POSTFIX = "PluginRealiginer.WrapPostfix";

	@NonNls
	private static final String PROPERTY_WRAP_ESCAPESINGLEQUOTES = "PluginRealiginer.WrapEscapeSingleQuotes";

	@NonNls
	private static final String PROPERTY_WRAP_ESCAPEDOUBLEQUOTES = "PluginRealiginer.WrapEscapeDoubleQuotes";

	@NonNls
	private static final String PROPERTY_WRAP_ESCAPEBACKSLASHES = "PluginRealiginer.WrapEscapeBackslashes";



	/**
	 * Store wrap Preferences
	 *
	 * @param	prefix					Wrap prefix string (LHS)
	 * @param	postfix					Wrap postfix string (RHS)
	 * @param	escapeSingleQuotes		Escape single quote characters?
	 * @param	escapeDoubleQuotes		Escape double quote characters?
	 * @param	escapeBackslashes		Escape backslash characters?
	 */
	public static void saveWrapProperties(String prefix, String postfix, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes) {
		PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

		propertiesComponent.setValue(PROPERTY_WRAP_PREFIX, prefix);
		propertiesComponent.setValue(PROPERTY_WRAP_POSTFIX, postfix);
		propertiesComponent.setValue(PROPERTY_WRAP_ESCAPESINGLEQUOTES, escapeSingleQuotes ? "1" : "0");
		propertiesComponent.setValue(PROPERTY_WRAP_ESCAPEDOUBLEQUOTES, escapeDoubleQuotes ? "1" : "0");
		propertiesComponent.setValue(PROPERTY_WRAP_ESCAPEBACKSLASHES, escapeBackslashes ? "1" : "0");
	}



	/**
	 * Store wrap Preferences
	 *
	 * @param	glue	Join glue string
	 */
	public static void saveJoinProperties(String glue) {
		PropertiesComponent.getInstance().setValue(PROPERTY_JOIN_GLUE, glue);
	}



	/**
	 * Get wrap prefix preference
	 *
	 * @return	String
	 */
	public static String getJoinGlue() {
		String glue	= PropertiesComponent.getInstance().getValue(PROPERTY_JOIN_GLUE);
		if( glue == null || glue.equals("") ) glue	= ", ";

		return glue;
	}



	/**
	 * Get wrap prefix preference
	 *
	 * @return	String
	 */
	public static String getWrapPrefix() {
		String prefix	= PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_PREFIX);
		if( prefix == null ) prefix	= ", ";

		return prefix;
	}



	/**
	 * Get wrap postfix preference
	 *
	 * @return	String
	 */
	public static String getWrapPostfix() {
		String postfix	= PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_POSTFIX);
		if( postfix == null ) postfix	= ", ";

		return postfix;
	}



	/**
	 * Get wrap preference: escape single quotes
	 *
	 * @return	String
	 */
	public static Boolean getWrapEscapeSingleQuotes() {
		String val	= PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_ESCAPESINGLEQUOTES);

		return (val == null) || val.equals("1");
	}



	/**
	 * Get wrap preference: escape double quotes
	 *
	 * @return	String
	 */
	public static Boolean getWrapEscapeDoubleQuotes() {
		String val	= PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_ESCAPEDOUBLEQUOTES);

		return (val == null) || val.equals("1");
	}



	/**
	 * Get wrap preference: Escape backslashes
	 *
	 * @return	String
	 */
	public static Boolean getWrapEscapeBackslashes() {
		String val	= PropertiesComponent.getInstance().getValue(PROPERTY_WRAP_ESCAPEBACKSLASHES);

		return (val == null) || val.equals("1");
	}

}
