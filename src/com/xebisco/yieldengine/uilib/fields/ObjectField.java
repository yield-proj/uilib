package com.xebisco.yieldengine.uilib.fields;

import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class ObjectField extends EditableField {
    private Serializable value;
    private Runnable applyRunnable;

    public ObjectField(String name, Serializable value) {
        this.value = value;
        setLayout(new BorderLayout());

        Pair<Runnable, JPanel> v = UIUtils.getFieldsPanel(value, false, null, null);
        applyRunnable = v.first();

        add(v.second(), BorderLayout.CENTER);

        if (name != null) {
            v.second().setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            JLabel label = new JLabel(UIUtils.prettyString(name));
            JPanel p = new JPanel(new BorderLayout());
            p.setPreferredSize(new Dimension(40, 40));
            p.add(label, BorderLayout.WEST);

            JPanel separatorPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(getForeground());
                    g.drawLine(10, getHeight() / 2, getWidth(), getHeight() / 2);
                }
            };
            p.add(separatorPanel, BorderLayout.CENTER);

            add(p, BorderLayout.NORTH);
        }
    }

    @Override
    public Serializable getValue() {
        applyRunnable.run();
        return value;
    }
}
