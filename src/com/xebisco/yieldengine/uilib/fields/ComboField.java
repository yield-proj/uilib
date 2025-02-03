package com.xebisco.yieldengine.uilib.fields;

import com.xebisco.yieldengine.uilib.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ComboField extends EditableField {

    private final JComboBox<String> comboBox;

    public ComboField(String name, String value, Method valuesMethod, boolean editable) {
        setLayout(new BorderLayout(5, 0));
        add(UIUtils.nameLabel(name), BorderLayout.WEST);
        try {
            comboBox = new JComboBox<>((String[]) valuesMethod.invoke(null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        comboBox.setSelectedItem(value);
        comboBox.setEditable(editable);
        ((JTextField) comboBox.getEditor().getEditorComponent()).setEditable(false);
        add(comboBox);
    }

    @Override
    public String getValue() {
        return (String) comboBox.getSelectedItem();
    }
}
