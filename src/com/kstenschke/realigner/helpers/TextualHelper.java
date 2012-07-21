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
	 * @param str
	 * @param subStr
	 * @return
	 */
	public static int substrCount(String str, String subStr) {
		if(str.equals("") || subStr.equals("")) {
			return 0;
		}

		int lastIndex = 0;
		int count =0;

		while(lastIndex != -1){
			lastIndex = str.indexOf(subStr,lastIndex);

			if( lastIndex != -1){
				count ++;
				lastIndex+=subStr.length();
			}
		}

		return count;
	}



	/**
	 * Get sub sequence
	 *
	 * @param text
	 * @param offsetStart
	 * @param offsetEnd
	 * @return
	 */
	public static String getSubString(CharSequence text, int offsetStart, int offsetEnd) {
		if (text.length() == 0) return null;

		return text.subSequence(offsetStart, offsetEnd).toString();
	}



	/**
	 * @param doc
	 * @param startLine
	 * @param endLine
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
