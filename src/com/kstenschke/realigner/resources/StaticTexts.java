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
package com.kstenschke.realigner.resources;

import org.jetbrains.annotations.NonNls;

public class StaticTexts {

    // Dialogs
    @NonNls
    public static final String MESSAGE_TITLE_SPLIT = "Split by Delimiter";
    @NonNls
    public static final String MESSAGE_TITLE_WRAP = "Wrap Text";
    @NonNls
    public static final String MESSAGE_TITLE_JOIN = "Join Lines with Glue";

    // Context menu items
    @NonNls
    public static final String POPUP_QUICKWRAP_REMOVE = "Remove wrap";

    // Notification messages
    @NonNls
    public static final String NOTIFICATION_SPLIT_DELIMITER_MISSING = "Delimiter not found.";
    @NonNls
    public static final String NOTIFICATION_JOIN_NO_LINES_SELECTED = "Please select lines to be joined.";

    // Undo history
    @NonNls
    public static final String UNDO_HISTORY_SPLIT = "Split into Lines";
    @NonNls
    public static final String UNDO_HISTORY_JOIN = "Join Lines with Glue";
    @NonNls
    public static final String UNDO_HISTORY_WRAP = "Wrap Text";
    @NonNls
    public static final String UNDO_HISTORY_UNWRAP = "Unwrap Text";
}
