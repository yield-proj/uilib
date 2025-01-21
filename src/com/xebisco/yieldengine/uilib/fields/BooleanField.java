package com.xebisco.yieldengine.uilib.fields;

import javax.swing.*;
import java.awt.*;

public class BooleanField extends EditableField {
    private final JCheckBox checkBox;

    public BooleanField(String name, Boolean value, boolean editable) {
        checkBox = new JCheckBox(name, value);
        checkBox.setEnabled(editable);
        setLayout(new BorderLayout());
        add(checkBox, BorderLayout.CENTER);
    }

    @Override
    public Boolean getValue() {
        return checkBox.isSelected();
    }
}
