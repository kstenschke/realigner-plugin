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
package com.kstenschke.realigner.listeners;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Accelerate navigation by adding quick-button tabbing via cursor up/down keys
 */
public class KeyListenerCursorUpDown implements KeyListener {

    private Component componentAbove = null;
    private Component componentUnder = null;

    public KeyListenerCursorUpDown(@Nullable Component componentUnder, @Nullable Component componentAbove) {
        this.componentAbove = componentAbove;
        this.componentUnder = componentUnder;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                this.focusComponent(this.componentUnder);
                break;

            case KeyEvent.VK_UP:
                this.focusComponent(this.componentAbove);
                break;
        }
    }

    /**
     * @param   component
     */
    private void focusComponent(Component component) {
        if (null != component) {
            component.requestFocusInWindow();
        }
    }
}
