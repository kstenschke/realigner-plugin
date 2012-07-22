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

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class TextualHelper {

	/**
	 * Returns amount of occurrences of sub string in string
	 *
	 * @param haystack   String to be searched in
	 * @param needle     Sub string
	 * @return           Amount of occurrences
	 */
	public static int substrCount(String haystack, String needle) {
		if(haystack.equals("") || needle.equals("")) {
			return 0;
		}

		int lastIndex = 0;
		int count =0;

		while(lastIndex != -1){
			lastIndex = haystack.indexOf(needle,lastIndex);

			if( lastIndex != -1){
				count ++;
				lastIndex+=needle.length();
			}
		}

		return count;
	}



	/**
	 * Get sub sequence from given offset region
	 *
	 * @param haystack         Text from which the sub string is to be extracted
	 * @param offsetStart      Starting offset
	 * @param offsetEnd        Ending offset
	 * @return
	 */
	public static String getSubString(CharSequence haystack, int offsetStart, int offsetEnd) {
		if (haystack.length() == 0) return null;

		return haystack.subSequence(offsetStart, offsetEnd).toString();
	}



	/**
	 * @param doc           The full document
	 * @param startLine     Starting line number
	 * @param endLine       Ending line number
	 * @return
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
	 * @param doc
	 * @param lineNumber
	 * @return
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

}
