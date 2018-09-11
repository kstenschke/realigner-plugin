/*
 * Copyright 2012-2018 Kay Stenschke
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
package com.kstenschke.realigner.utils;

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
        return haystack.length() == 0
                ? null
                : haystack.subSequence(offsetStart, offsetEnd).toString();
    }

    /**
     * @param   doc            The full document
     * @param   startLine      Starting line number
     * @param   endLine         Ending line number
     * @return  List<String>
     */
    public static List<String> extractLines(Document doc, int startLine, int endLine) {
        List<String> lines = new ArrayList<>(endLine - startLine);

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
        return lineSeparatorLength == 0
                ? line + "\n"
                : line;
    }

    /**
     * @param   str         String to be checked for containing an HTML tag
     * @return  boolean     Does the given string contain an HTML tag?
     */
    private static boolean isHtmlTag(String str) {
        String regex = "<[a-z|A-Z]+(.| )*>.*";

        return str.matches(regex);
    }

    private static String getHtmlTagCounterpart(String prefix) {
        prefix = prefix.replaceAll("<", "");
        String[] tag = prefix.split("\\W+");

        return "</" + tag[0] + ">";
    }

    /**
     * @param   str     String to be transformed
     * @param   prefix  Prefix to be removed
     * @param   postfix Postfix to be removed
     * @return  String  Given string with prefix and postfix removed
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

    public static String lTrim(String str) {
        int i = 0;
        while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
            i++;
        }

        return str.substring(i);
    }

    /**
     * @param   str
     * @return  Amount of tabs in given string
     */
    public static Integer countTabOccurrences(String str) {
        return str.length() - str.replaceAll("\t", "").length();
    }

    /**
     * @param   text
     * @return  String  All lines being trimmed from leading and trailing whitespace
     */
    public static String trimLines(String text) {
        String[] lines  = text.split("\n");
        String result   = "";
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() /*|| keepEmptyLines*/) {
                result += line + "\n";
            }
        }

        return result;
    }

    /**
     *
     * @param   strLHS      Left hand side string
     * @return  Right hand side (counterpart) string
     */
    public static String getWrapCounterpart(String strLHS) {
        if (strLHS.isEmpty()) {
            return "";
        }

        strLHS = strLHS.trim();
        if (UtilsTextual.isHtmlTag(strLHS)) {
            // HTML tag: fill with postfix field with resp. pendent
            return UtilsTextual.getHtmlTagCounterpart(strLHS);
        }
        if (strLHS.startsWith("(") && ! strLHS.contains(")")) {
            return ")";
        }
        if (strLHS.startsWith("[") && ! strLHS.contains("]")) {
            return "]";
        }
        if (strLHS.startsWith("{") && ! strLHS.contains("}")) {
            return "}";
        }
        if (strLHS.startsWith("«") && ! strLHS.contains("»")) {
            return "»";
        }
        if (strLHS.startsWith("„") && ! strLHS.contains("“")) {
            return "“";
        }
        if (strLHS.startsWith("“") && ! strLHS.contains("”")) {
            return "”";
        }
        if (strLHS.startsWith("‘") && ! strLHS.contains("’")) {
            return "’";
        }
        if (strLHS.startsWith("<!--") && ! strLHS.contains("-->")) {
            return "-->";
        }
        if (strLHS.startsWith("<") && ! strLHS.contains(">")) {
            return ">";
        }
        if (strLHS.startsWith("/*") && ! strLHS.contains("*/")) {
            return "*/";
        }

        return null;
    }
}
