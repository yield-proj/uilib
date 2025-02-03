package com.xebisco.yieldengine.uilib;

import com.formdev.flatlaf.icons.FlatClearIcon;
import com.formdev.flatlaf.ui.FlatRoundBorder;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CheckedComboBox extends JComboBox<CheckedComboBox.CheckItem> {
    protected boolean keepOpen;
    private final JLabel label = new JLabel("TEST");
    private final JPanel panel = new JPanel(new BorderLayout());

    public CheckedComboBox(DefaultComboBoxModel<CheckItem> model) {
        super(model);
        setRenderer(new CheckBoxCellRenderer<>());
        JTextField f = (JTextField) getEditor().getEditorComponent();
        f.setCaretColor(new Color(0, 0, 0, 0));
        f.setEditable(false);
        f.setOpaque(false);
        f.setText(" ");
        f.setLayout(new BorderLayout());
        panel.setOpaque(false);
        f.add(panel);

        updatePanel();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 22);
    }

    @Override
    public void updateUI() {
        super.updateUI();

        Accessible a = getAccessibleContext().getAccessibleChild(0);
        if (a instanceof ComboPopup) {
            ((ComboPopup) a).getList().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JList<?> list = (JList<?>) e.getComponent();
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        keepOpen = true;
                        updateItem(list.locationToIndex(e.getPoint()));
                    }
                }
            });
        }

        JCheckBox check = new JCheckBox();
        check.setOpaque(false);
        initActionMap();
    }

    private void updatePanel() {
        panel.removeAll();

        JPanel argumentsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        argumentsPanel.setOpaque(false);

        JScrollPane argsScrollPane = new JScrollPane() {
            @Override
            public JScrollBar createHorizontalScrollBar() {
                JScrollBar horizontal = new JScrollPane.ScrollBar(Adjustable.HORIZONTAL);
                horizontal.setPreferredSize(new Dimension(0, 0));
                return horizontal;
            }
        };
        argsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        argsScrollPane.getVerticalScrollBar().setUnitIncrement(0);
        argsScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        argsScrollPane.setOpaque(false);

        List<CheckItem> args = getCheckItems(getModel());
        if (args != null) {
            for (int i = 0; i < args.size(); i++) {
                CheckItem arg = args.get(i);
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setText(" " + arg + "       ");
                label.setBorder(new FlatRoundBorder());
                label.setLayout(new BorderLayout());
                JButton button = new JButton(new AbstractAction("", new FlatClearIcon()) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        arg.setSelected(false);
                        updatePanel();
                    }
                });
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                label.add(button, BorderLayout.EAST);
                argumentsPanel.add(label);
            }
            argsScrollPane.setViewportView(argumentsPanel);
            panel.add(argsScrollPane);

            argumentsPanel.scrollRectToVisible(new Rectangle(5, 5, 64, 18));
        }

        panel.updateUI();
    }

    class CheckBoxCellRenderer<E extends CheckItem> implements ListCellRenderer<E> {
        private final JCheckBox check = new JCheckBox(" ");

        @Override
        public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
            if (index < 0) {
                return label;
            } else {
                check.setText(Objects.toString(value, ""));
                check.setSelected(value.isSelected());
                if (isSelected) {
                    check.setBackground(list.getSelectionBackground());
                    check.setForeground(list.getSelectionForeground());
                } else {
                    check.setBackground(list.getBackground());
                    check.setForeground(list.getForeground());
                }
                return check;
            }
        }
    }

    protected void initActionMap() {
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
        getInputMap(WHEN_FOCUSED).put(ks, "checkbox-select");
        getActionMap().put("checkbox-select", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Accessible a = getAccessibleContext().getAccessibleChild(0);
                if (a instanceof ComboPopup) {
                    updateItem(((ComboPopup) a).getList().getSelectedIndex());
                }
            }
        });
    }

    protected void updateItem(int index) {
        if (isPopupVisible() && index >= 0) {
            CheckItem item = getItemAt(index);
            item.setSelected(!item.isSelected());
            // item.selected ^= true;
            // ComboBoxModel m = getModel();
            // if (m instanceof CheckedComboBoxModel) {
            //   ((CheckedComboBoxModel) m).fireContentsChanged(index);
            // }
            // removeItemAt(index);
            // insertItemAt(item, index);
            setSelectedIndex(-1);
            setSelectedItem(item);
        }
    }

    @Override
    public void setPopupVisible(boolean v) {
        if (keepOpen) {
            keepOpen = false;
        } else {
            super.setPopupVisible(v);
        }
        updatePanel();
    }

    protected static <E extends CheckItem> String getCheckItemString(ListModel<E> model) {
        return IntStream.range(0, model.getSize())
                .mapToObj(model::getElementAt)
                .filter(CheckItem::isSelected)
                .map(Objects::toString)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    protected static <E extends CheckItem> List<CheckItem> getCheckItems(ListModel<CheckItem> model) {
        return IntStream.range(0, model.getSize())
                .mapToObj(model::getElementAt)
                .filter(CheckItem::isSelected)
                .toList();
    }


    public static class CheckItem {
        private final String text;
        private boolean selected;

        public CheckItem(String text, boolean selected) {
            this.text = text;
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean isSelected) {
            selected = isSelected;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}