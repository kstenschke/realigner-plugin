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

import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.awt.*;

public class UtilsEnvironment {

    /**
     * @param editor
     * @param dialog
     */
    public static void setDialogVisible(Editor editor, JDialog dialog, String title) {
        Point caretLocation  = editor.visualPositionToXY(editor.getCaretModel().getVisualPosition());
        SwingUtilities.convertPointToScreen(caretLocation, editor.getComponent());

        if( caretLocation.getY() >= java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() ) {
                // Comprehend for (idea ultimate edition) off-screen caret position
            dialog.setLocationRelativeTo(null);
        } else {
            dialog.setLocation(caretLocation);
        }

        dialog.setTitle(title);

        dialog.pack();
        dialog.setVisible(true);
    }

}