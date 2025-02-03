package com.xebisco.yieldengine.uilib.fields;

import com.xebisco.yieldengine.uilib.CheckedComboBox;
import com.xebisco.yieldengine.uilib.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultiComboField extends EditableField {

    private final CheckedComboBox comboBox;

    public MultiComboField(String name, String[] value, Method valuesMethod, boolean editable) {
        setLayout(new BorderLayout(5, 0));
        add(UIUtils.nameLabel(name), BorderLayout.WEST);
        try {
            String[] arr = (String[]) valuesMethod.invoke(null);
            CheckedComboBox.CheckItem[] items = new CheckedComboBox.CheckItem[arr.length];
            for (int i = 0; i < arr.length; i++) {
                boolean selected = false;
                for(String v : value) {
                    if (arr[i].equals(v)) {
                        selected = true;
                        break;
                    }
                }
                items[i] = new CheckedComboBox.CheckItem(arr[i], selected);
            }
            comboBox = new CheckedComboBox(new DefaultComboBoxModel<>(items));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        comboBox.setEditable(editable);
        add(comboBox);
    }

    @Override
    public String[] getValue() {
        Stream<String> s = IntStream.range(0, comboBox.getModel().getSize())
                .mapToObj(comboBox.getModel()::getElementAt)
                .filter(CheckedComboBox.CheckItem::isSelected).map(CheckedComboBox.CheckItem::toString);
        return s.toArray(String[]::new);
    }
}
