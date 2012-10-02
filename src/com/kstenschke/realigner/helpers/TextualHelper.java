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

package com.kstenschke.realigner.helpers;

import com.intellij.openapi.editor.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class TextualHelper {

	/**
	 * Get sub sequence from given offset region
	 *
	 * @param	haystack		Text from which the sub string is to be extracted
	 * @param	offsetStart		Starting offset
	 * @param	offsetEnd		Ending offset
	 * @return					String
	 */
	public static String getSubString(CharSequence haystack, int offsetStart, int offsetEnd) {
		if (haystack.length() == 0) return null;

		return haystack.subSequence(offsetStart, offsetEnd).toString();
	}



	/**
	 * @param	doc				The full document
	 * @param	startLine		Starting line number
	 * @param	endLine			Ending line number
	 * @return	List<String>
	 */
	public static List<String> extractLines(Document doc, int startLine, int endLine) {
		List<String> lines = new ArrayList<String>(endLine - startLine);

		for (int i = startLine; i <= endLine; i++) {
			String line = TextualHelper.extractLine(doc, i);

			lines.add(line);
		}

		return lines;
	}



	/**
	 * @param	doc				The full document
	 * @param	lineNumber		Number of line to be extracted
	 * @return	String			The extracted line
	 */
	public static String extractLine(Document doc, int lineNumber) {
		int lineSeparatorLength = doc.getLineSeparatorLength(lineNumber);
		int startOffset = doc.getLineStartOffset(lineNumber);
		int endOffset = doc.getLineEndOffset(lineNumber) + lineSeparatorLength;

		String line = doc.getCharsSequence().subSequence(startOffset, endOffset).toString();

		// If last line has no \n, add it one
		// This causes adding a \n at the end of file when sort is applied on whole file and the file does not end
		// with \n... This is fixed after.
		if (lineSeparatorLength == 0) {
			line += "\n";
		}

		return line;
	}



	/**
	 * @param	str						String with special chars to be escaped
	 * @param	escapeSingleQuotes		' to \' ?
	 * @param	escapeDoubleQuotes		" to \" ?
	 * @param	escapeBackslashes		\ to \\ ?
	 * @return							The escaped string
	 */
	public static String escapeSelectively(String str, Boolean escapeSingleQuotes, Boolean escapeDoubleQuotes, Boolean escapeBackslashes) {
			// Escape backslashes - important: must be done before quotes, as their escaping adds more backslashes!
		if( escapeBackslashes ) {
			str	= str.replaceAll("\\\\", "\\\\\\\\");
		}

			// Escape single quotes
		if( escapeSingleQuotes ) {
			str = str.replaceAll("'", "\\\\\'");
		}

			// Escape double quotes
		if( escapeDoubleQuotes ) {
			str = str.replaceAll("\"", "\\\\\\\"");
		}

		return str;
	}



	/**
	 * @param	str
	 * @param	regex
	 * @return  		Amount of matches of regex in str
	 */
	public static Integer getAmountMatches(String str, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);

		int count = 0;
		while ( matcher.find() ) count++;

		return count;
	}

}
