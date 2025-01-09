package com.xebisco.yieldengine.uilib.projectmng;

import com.formdev.flatlaf.icons.FlatHelpButtonIcon;
import com.xebisco.yieldengine.uilib.*;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class OptionsFrame extends JFrame {
    private final JSplitPane splitPane;

    public OptionsFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setTitle(title());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        JPanel tabsPanel = new JPanel(new BorderLayout());
        JTree tabsTree = new JTree();
        tabsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tabsTree.setRowHeight(0);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.setUserObject(tabsTree);
        Pair<Runnable, DefaultMutableTreeNode[]> tabs = tabs();
        for (DefaultMutableTreeNode tab : tabs.second()) {
            root.add(tab);
        }
        //tabsTree.setBackground(getBackground());
        tabsTree.setModel(new DefaultTreeModel(root));
        SearchBar<String> searchBar = new SearchBar<>();
        searchBar.setOpaque(false);
        searchBar.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        FilteredListModel.Filter<String> filter = element -> element.toUpperCase().contains(searchBar.getText().toUpperCase());
        UIUtils.addSearchToTree(searchBar, tabsTree, root, filter);
        tabsTree.setCellRenderer(new OptionsTreeCellRenderer(filter));
        tabsPanel.add(searchBar, BorderLayout.NORTH);
        tabsTree.addTreeSelectionListener(e -> {
            if (tabsTree.getSelectionPath() == null) return;
            JPanel tabPanel = new MainPanel(((JPanel) ((DefaultMutableTreeNode) tabsTree.getSelectionPath().getLastPathComponent()).getUserObject()), UIUtils.getTreeTitle(((DefaultMutableTreeNode) tabsTree.getSelectionPath().getLastPathComponent())));
            tabPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            splitPane.setRightComponent(tabPanel);
            splitPane.updateUI();
        });
        JScrollPane tabsScroll = new JScrollPane(tabsTree);
        tabsScroll.setBorder(BorderFactory.createEmptyBorder());
        tabsScroll.getVerticalScrollBar().setUnitIncrement(16);
        tabsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tabsScroll.setBackground(tabsTree.getBackground());
        tabsPanel.setBackground(tabsTree.getBackground());
        tabsPanel.add(tabsScroll, BorderLayout.CENTER);
        tabsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        splitPane.setLeftComponent(tabsPanel);

        add(splitPane);

        tabsTree.setSelectionRow(1);

        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(.25));

        JPanel buttonPanel = new JPanel(new BorderLayout());

        if (help() != null) {
            JButton helpButton = new JButton(new AbstractAction("", new FlatHelpButtonIcon()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    help();
                }
            });
            helpButton.setBorderPainted(false);
            helpButton.setContentAreaFilled(false);
            buttonPanel.add(helpButton, BorderLayout.WEST);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        }
        if (tabs.first() != null)
            buttonPanel.add(UIUtils.getButtonPanel(true, () -> tabs.first().run(), () -> SwingUtilities.getWindowAncestor(buttonPanel).dispose()), BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public abstract String title();

    public abstract Pair<Runnable, DefaultMutableTreeNode[]> tabs();

    public abstract Runnable help();
}
