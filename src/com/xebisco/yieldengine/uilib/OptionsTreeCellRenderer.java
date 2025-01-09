package com.xebisco.yieldengine.uilib;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class OptionsTreeCellRenderer extends DefaultTreeCellRenderer {
    private final FilteredListModel.Filter<String> filter;

    public OptionsTreeCellRenderer(FilteredListModel.Filter<String> filter) {
        this.filter = filter;
    }


    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (UIUtils.shouldHide((DefaultMutableTreeNode) value, filter)) {
            JLabel nullLabel = new JLabel();
            nullLabel.setPreferredSize(new Dimension(0, 0));
            return nullLabel;
        }
        Component c = super.getTreeCellRendererComponent(tree, ((Component) ((DefaultMutableTreeNode) value).getUserObject()).getName(), sel, expanded, leaf, row, hasFocus);
        c.setPreferredSize(new Dimension(600, 30));
        if (((DefaultMutableTreeNode) value).getLevel() == 1)
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        else c.setFont(c.getFont().deriveFont(Font.PLAIN));
        return c;
    }

    public FilteredListModel.Filter<String> getFilter() {
        return filter;
    }
}
