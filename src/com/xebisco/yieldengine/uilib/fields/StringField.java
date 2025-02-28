package com.xebisco.yieldengine.uilib.fields;

import com.xebisco.yieldengine.uilib.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class StringField extends EditableField {
    private final JTextField textField;

    public StringField(String name, String value, boolean editable) {
        if(value == null) value = "";
        setLayout(new BorderLayout(5, 0));
        add(UIUtils.nameLabel(name), BorderLayout.WEST);
        textField = new JTextField(value);
        textField.setEditable(editable);
        add(textField);
    }

    @Override
    public Serializable getValue() {
        return textField.getText();
    }
}
