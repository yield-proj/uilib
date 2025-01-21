package com.xebisco.yieldengine.uilib.fields;

import com.xebisco.yieldengine.uilib.UIUtils;
import org.joml.Vector2f;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Vector2Field extends EditableField {
    private final NumberField<Float> xField, yField;

    public Vector2Field(String name, Vector2f vec, boolean editable) {
        setLayout(new BorderLayout());
        xField = new NumberField<>("X", vec.x, Float.class, editable);
        yField = new NumberField<>("Y", vec.y, Float.class, editable);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.X_AXIS));
        fields.add(Box.createHorizontalStrut(5));
        fields.add(xField);
        fields.add(Box.createHorizontalStrut(5));
        fields.add(yField);

        add(fields, BorderLayout.CENTER);

        add(UIUtils.nameLabel(name), BorderLayout.WEST);
    }

    @Override
    public Vector2f getValue() {
        return new Vector2f(xField.getValue(), yField.getValue());
    }
}
