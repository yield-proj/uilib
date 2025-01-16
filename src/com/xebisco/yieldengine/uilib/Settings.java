package com.xebisco.yieldengine.uilib;

import com.xebisco.yieldengine.uilib.projectmng.OptionsFrame;
import com.xebisco.yieldengine.uilib.projectmng.Project;
import com.xebisco.yieldengine.utils.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Settings implements Serializable {
    @Serial
    public static final long serialVersionUID = -2291406085025856206L;
    public static Settings INSTANCE;
    public static String APP_NAME;

    public Workspace workspace = new Workspace();

    class SettingsWindow extends OptionsFrame {
        @Override
        public String title() {
            return "Settings";
        }

        @Override
        public Pair<Runnable, DefaultMutableTreeNode[]> tabs() {
            Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs = Settings.this.tabs();
            return new Pair<>(() -> tabs.first().forEach(Runnable::run), tabs.second().toArray(new DefaultMutableTreeNode[0]));
        }

        @Override
        public Runnable help() {
            return Settings.this.help();
        }
    }

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

        return new Pair<>(workspaceTab.first(), user);
    }

    protected Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs() {
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<>();

        ArrayList<Runnable> applyList = new ArrayList<>();

        Pair<Runnable, DefaultMutableTreeNode> userTab = userTab();
        UIUtils.depthPanel(userTab.second(), "User");

        nodes.add(userTab.second());

        return new Pair<>(applyList, nodes);
    }

    public static void loadSettings(Class<? extends Settings> settingsClass) {
        if (INSTANCE == null) {
            throw new IllegalStateException("Settings INSTANCE is null");
        }
        File dir = new File(FileUtils.getWorkingDirectory(), APP_NAME);
        //noinspection ResultOfMethodCallIgnored
        dir.mkdir();
        File settingsFile = new File(dir, "yield_settings.ser");
        if (settingsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settingsFile))) {
                INSTANCE = (Settings) ois.readObject();
            } catch (Exception e) {
                UIUtils.error(e);
                System.exit(12);
            }
        } else {
            try {
                INSTANCE = settingsClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                UIUtils.error(e);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settingsFile))) {
                oos.writeObject(INSTANCE);
            } catch (IOException e) {
                UIUtils.error(e);
            }
        }, "SaveSettings"));
    }

    public void openSettings(Frame frame) {
        JFrame f = new SettingsWindow();
        f.setLocationRelativeTo(frame);

        f.setVisible(true);
    }

    public static class Workspace implements Serializable {
        @Visible
        @Editable
        private String name = "MyWorkspace";

        @Visible
        @Editable
        @FileExtensions(value = {}, acceptDirectories = true)
        private File directory = new File(System.getProperty("user.home"), "YieldWorkspace");

        @Visible
        @Editable
        private Project[] projects = new Project[0];

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

        public Project[] getProjects() {
            return projects;
        }

        public Workspace setProjects(Project[] projects) {
            this.projects = projects;
            return this;
        }
    }

}
