package com.xebisco.yieldengine.uilib;

import com.xebisco.yieldengine.utils.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class BasicSettings implements Serializable {
    @Serial
    public static final long serialVersionUID = -2291406085025856206L;

    public Workspace workspace = new Workspace();



    protected Runnable help() {
        return () -> UIUtils.about(null, "", "Help Page");
    }

    protected Pair<Runnable, DefaultMutableTreeNode> workspaceTab() {
        JPanel workspacePanel = new JPanel(new BorderLayout());
        workspacePanel.setName("Workspace");
        Pair<Runnable, JPanel> v = UIUtils.getObjectsFieldsPanel(new Object[]{workspace});
        workspacePanel.add(v.second());
        return new Pair<>(v.first(), new DefaultMutableTreeNode(workspacePanel));
    }

    protected Pair<Runnable, DefaultMutableTreeNode> userTab() {
        DefaultMutableTreeNode user = new DefaultMutableTreeNode();
        Pair<Runnable, DefaultMutableTreeNode> workspaceTab = workspaceTab();
        user.add(workspaceTab.second());

        return new Pair<>(() -> {
            workspaceTab.first().run();
        }, user);
    }

    protected Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs() {
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<>();

        ArrayList<Runnable> applyList = new ArrayList<>();

        Pair<Runnable, DefaultMutableTreeNode> userTab = userTab();
        UIUtils.depthPanel(userTab.second(), "User");

        nodes.add(userTab.second());
        applyList.add(userTab.first());

        return new Pair<>(applyList, nodes);
    }

    public static class Workspace implements Serializable {
        @Serial
        private static final long serialVersionUID = 7366515736799507122L;

        @Visible
        @Editable
        private String name = "MyWorkspace";

        @Visible
        @Editable
        @FileExtensions(value = {}, acceptDirectories = true)
        private File directory = new File(System.getProperty("user.home"), "YieldWorkspace");

        @Visible
        @Editable
        private File[] projects = new File[0];

        public String getName() {
            return name;
        }

        public Workspace setName(String name) {
            this.name = name;
            return this;
        }

        public File getDirectory() {
            return directory;
        }

        public Workspace setDirectory(File directory) {
            this.directory = directory;
            return this;
        }

        public File[] getProjects() {
            return projects;
        }

        public Workspace setProjects(File[] projects) {
            this.projects = projects;
            return this;
        }
    }

}
