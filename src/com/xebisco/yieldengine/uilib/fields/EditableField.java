package com.xebisco.yieldengine.uilib.fields;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public abstract class EditableField extends JPanel {
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getParent().getSize().width, super.getPreferredSize().height);
    }

    public abstract Serializable getValue();
}
