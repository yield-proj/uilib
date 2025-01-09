package com.xebisco.yieldengine.uilib;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface DocumentChangeListener extends DocumentListener {
    @Override
    default void insertUpdate(DocumentEvent e) {
        updateUpdate(e);
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        updateUpdate(e);
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        updateUpdate(e);
    }

    void updateUpdate(DocumentEvent e);
}
