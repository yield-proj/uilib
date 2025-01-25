package com.xebisco.yieldengine.uilib.fields;

import com.xebisco.yieldengine.uilib.NumberTextField;
import com.xebisco.yieldengine.uilib.UIUtils;

import java.awt.*;

public class NumberField<T extends Number> extends EditableField {
    private final NumberTextField<T> textField;

    public NumberField(String name, T value, Class<T> numberClass, boolean editable) {
        setLayout(new BorderLayout(5, 0));
        add(UIUtils.nameLabel(name), BorderLayout.WEST);
        textField = new NumberTextField<>(numberClass, true);
        textField.setEditable(editable);
        textField.setValue(value);

        add(textField);
    }

    @Override
    public T getValue() {
        return textField.getNumberValue();
    }
}