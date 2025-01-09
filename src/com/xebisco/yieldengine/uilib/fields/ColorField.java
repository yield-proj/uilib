package com.xebisco.yieldengine.uilib.fields;

import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.Color4f;
import com.xebisco.yieldengine.utils.ColorUtils;

import javax.swing.*;
import java.awt.*;

public class ColorField extends EditableField {
    private final JButton button;

    public ColorField(String name, Color4f value, boolean editable) {
        if (value == null) value = new Color4f(0, 0, 0, 1);
        setLayout(new BorderLayout(5, 0));

        add(UIUtils.nameLabel(name), BorderLayout.WEST);

        button = new JButton("Color");
        button.setFocusPainted(false);
        button.setEnabled(editable);

        button.setBackground(UIUtils.getColor(value));
        button.setToolTipText("#" + Integer.toHexString(ColorUtils.argb(value)).toUpperCase());

        button.addActionListener(_ -> {
            Color newColor = JColorChooser.showDialog(ColorField.this, "Choose Color", UIUtils.getColor(getValue()));

            if(newColor != null) {
                button.setBackground(newColor);
                button.setToolTipText("#" + Integer.toHexString(newColor.getRGB()).toUpperCase());
            }
        });

        add(button);
    }

    @Override
    public Color4f getValue() {
        return ColorUtils.argb(button.getBackground().getRGB());
    }
}
