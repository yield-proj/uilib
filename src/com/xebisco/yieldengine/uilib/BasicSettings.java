package com.xebisco.yieldengine.uilib;

import com.xebisco.yieldengine.utils.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BasicSettings implements Serializable {
    @Serial
    public static final long serialVersionUID = -2291406085025856206L;

    public Workspace WORKSPACE;

    public BasicSettings() {
        for(Field f : getClass().getFields()) {
            try {
                if(f.get(this) == null) {
                    f.set(this, f.getType().getConstructor().newInstance());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected Runnable help() {
        return () -> UIUtils.about(null, "", "Help Page");
    }

    public static void addSection(Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs, String sectionName, Object... innerTabs) {
        DefaultMutableTreeNode editorSettings = new DefaultMutableTreeNode();

        List<Runnable> applyList = new ArrayList<>();

        for (Object tab : innerTabs) {
            Pair<Runnable, DefaultMutableTreeNode> tabPair = createTab(tab);
            editorSettings.add(tabPair.second());
            applyList.add(tabPair.first());
        }
        Pair<Runnable, DefaultMutableTreeNode> editorSettingsTab = new Pair<>(() -> applyList.forEach(Runnable::run), editorSettings);
        UIUtils.depthPanel(editorSettingsTab.second(), sectionName);

        tabs.second().add(editorSettingsTab.second());
        tabs.first().add(editorSettingsTab.first());
    }

    protected static Pair<Runnable, DefaultMutableTreeNode> createTab(Object tab) {
        JPanel p = new JPanel(new BorderLayout());
        p.setName(UIUtils.prettyString(tab.getClass().getSimpleName()));
        Pair<Runnable, JPanel> v = UIUtils.getObjectsFieldsPanel(new Object[]{tab});
        p.add(v.second());
        return new Pair<>(v.first(), new DefaultMutableTreeNode(p));
    }

    protected Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs() {
        Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs = new Pair<>(new ArrayList<>(), new ArrayList<>());

        addSection(tabs, "User", WORKSPACE);

        return tabs;
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
