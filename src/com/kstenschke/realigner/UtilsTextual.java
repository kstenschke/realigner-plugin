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

package com.kstenschke.realigner;

import com.intellij.openapi.editor.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class UtilsTextual {

	/**
	 * Get sub sequence from given offset region
	 *
	 * @param   haystack        Text from which the sub string is to be extracted
	 * @param   offsetStart     Starting offset
	 * @param   offsetEnd       Ending offset
	 * @return  String
	 */
	public static String getSubString(CharSequence haystack, int offsetStart, int offsetEnd) {
		if (haystack.length() == 0) return null;

		return haystack.subSequence(offsetStart, offsetEnd).toString();
	}

	/**
	 * @param   doc            The full document
	 * @param   startLine      Starting line number
	 * @param   endLine         Ending line number
	 * @return  List<String>
	 */
	public static List<String> extractLines(Document doc, int startLine, int endLine) {
		List<String> lines = new ArrayList<String>(endLine - startLine);

		for (int i = startLine; i <= endLine; i++) {
			String line = UtilsTextual.extractLine(doc, i);

			lines.add(line);
		}

		return lines;
	}

	/**
	 * @param   doc            The full document
	 * @param   lineNumber     Number of line to be extracted
	 * @return  String         The extracted line
	 */
	public static String extractLine(Document doc, int lineNumber) {
		int lineSeparatorLength = doc.getLineSeparatorLength(lineNumber);
		int startOffset         = doc.getLineStartOffset(lineNumber);
		int endOffset           = doc.getLineEndOffset(lineNumber) + lineSeparatorLength;

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
	 * @param   str         String to be checked for containing an HTML tag
	 * @return  Boolean     Does the given string contain an HTML tag?
	 */
	public static Boolean containsHtmlTag(String str) {
		String regex = "<[a-z|A-Z]+(.| )*>.*";

		return str.matches(regex);
	}

    /**
     * @param   prefix
     * @return  String
     */
	public static String getClosingTagPendent(String prefix) {
		prefix = prefix.replaceAll("<", "");
		String[] tag = prefix.split("\\W+");

		return "</" + tag[0] + ">";
	}

	/**
	 * @param   str     String to be transformed
	 * @param   prefix  Prefix to be removed
	 * @param   postfix Postfix to be removed
	 * @return Given string with prefix and postfix removed
	 */
	public static String unwrap(String str, String prefix, String postfix) {
		if (str.startsWith(prefix)) {
			str = str.substring(prefix.length());
		}
		if (str.endsWith(postfix)) {
			str = str.substring(0, str.length() - postfix.length());
		}

		return str;
	}

    /**
     * @param   str
     * @return  String
     */
    public static String ltrim(String str) {
        int i = 0;
        while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
            i++;
        }
        return str.substring(i);
    }

	/**
	 * @param   str
	 * @param   subStr
	 * @return Amount of occurrences of given substring in given string
	 */
	public static Integer countSubstringOccurrences(String str, String subStr) {
		return str.length() - str.replaceAll(subStr, "").length();
	}

    /**
     * @param   text
     * @return  String
     */
    public static String getLeadingWhitespace(String text) {
        return text.replace( ltrim(text), "");
    }

}
