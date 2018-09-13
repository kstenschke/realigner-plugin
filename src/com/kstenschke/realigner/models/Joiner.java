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
package com.kstenschke.realigner.models;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.kstenschke.realigner.utils.UtilsTextual;

import java.util.List;

public class Joiner {

    public void joinLines(Document document, int lineNumberSelStart, int lineNumberSelEnd, String glue) {
        int offsetStart;
        int offsetEnd;
        List<String> linesList = UtilsTextual.extractLines(document, lineNumberSelStart, lineNumberSelEnd);
        String linesStr = "";
        int amountLines = linesList.size();
        for (int i = 0; i < amountLines; i++) {
            linesStr = linesStr + (i > 0 ? linesList.get(i).trim() : linesList.get(i)) + (i < (amountLines - 1) ? glue : "");
        }

        // Remove newlines
        String joinedLines = linesStr.replaceAll("(\\n)+", "");

        // Replace the full lines with themselves joined
        offsetStart = document.getLineStartOffset(lineNumberSelStart);
        offsetEnd = document.getLineEndOffset(lineNumberSelEnd);

        ApplicationManager.getApplication().runWriteAction(() -> document.replaceString(offsetStart, offsetEnd, joinedLines));
    }
}
