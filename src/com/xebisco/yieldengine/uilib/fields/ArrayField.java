package com.xebisco.yieldengine.uilib.fields;

import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.formdev.flatlaf.ui.FlatTableHeaderBorder;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.ArrayUtils;
import com.xebisco.yieldengine.utils.CustomAdd;
import com.xebisco.yieldengine.utils.ObjectUtils;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ArrayField extends EditableField {
    private Object[] array;
    private final Class<?> arrayClass;
    private final JPanel itemsPanel = new JPanel() {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(scrollPane.getViewport().getViewRect().width, super.getPreferredSize().height);
        }
    };
    private final JScrollPane scrollPane = new JScrollPane();
    private Runnable applyList;
    private final Field arrayField;

    public ArrayField(Field arrayField, Object[] array, Class<?> arrayClass, boolean editable) {
        if (array == null) array = (Object[]) Array.newInstance(arrayClass, 0);
        this.arrayField = arrayField;
        this.array = array;
        this.arrayClass = arrayClass;
        setLayout(new BorderLayout());
        JLabel label = new JLabel(UIUtils.prettyString(arrayField.getName()));
        JToolBar topToolBar = new JToolBar();
        topToolBar.setBorder(new FlatButtonBorder());
        topToolBar.setFloatable(false);
        topToolBar.setRollover(true);
        topToolBar.add(Box.createHorizontalStrut(5));
        topToolBar.add(label);
        topToolBar.add(Box.createHorizontalGlue());
        add(topToolBar, BorderLayout.NORTH);

        JButton addButton = new JButton("+");
        topToolBar.add(addButton);
        topToolBar.setEnabled(editable);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JButton removeButton = new JButton("-");

        topToolBar.add(removeButton);

        if (array.length == 0) removeButton.setEnabled(false);

        if (arrayField.isAnnotationPresent(CustomAdd.class)) {
            addButton.addActionListener(_ -> {
                reload();
                try {
                    Class<?> nrc = Class.forName(arrayField.getAnnotation(CustomAdd.class).addAction());
                    Object t = nrc.getDeclaredMethod("returnNewObject").invoke(nrc.getDeclaredConstructor().newInstance());
                    if (t != null)
                        this.array = ArrayUtils.insertLast(this.array, t);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                         ClassNotFoundException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
                removeButton.setEnabled(true);
                reload();
            });
        } else {
            addButton.addActionListener(_ -> {
                reload();
                this.array = ArrayUtils.insertLast(this.array, ObjectUtils.newInstance(arrayClass));
                removeButton.setEnabled(true);
                reload();
            });
        }
        removeButton.addActionListener(_ -> {
            if (this.array.length == 0) return;
            reload();
            this.array = ArrayUtils.length(this.array, this.array.length - 1);
            if (this.array.length == 0) removeButton.setEnabled(false);
            reload();
        });

        topToolBar.add(Box.createHorizontalStrut(5));
        scrollPane.setEnabled(editable);
        scrollPane.setBorder(new FlatTableHeaderBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setViewportView(itemsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
        reload();
    }

    private void reload() {
        if (applyList != null) applyList.run();
        itemsPanel.removeAll();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.X_AXIS));
        Pair<Runnable, JPanel> r = UIUtils.getObjectsFieldsPanel(array, false, null, null, (from, to) -> {
            if (applyList != null) applyList.run();
            if (to == -1) {
                array = ArrayUtils.remove(array, from);
            } else {
                array = ArrayUtils.shift(array, from, to);
            }
            reload();
        }, arrayField);
        applyList = r.first();
        itemsPanel.add(r.second());
        updateUI();
    }

    @Override
    public Object[] getValue() {
        if (applyList != null) applyList.run();
        return array;
    }

    public Object[] getArray() {
        return array;
    }

    public ArrayField setArray(Object[] array) {
        this.array = array;
        return this;
    }

    public Class<?> getArrayClass() {
        return arrayClass;
    }

    public JPanel getItemsPanel() {
        return itemsPanel;
    }
}
