package com.xebisco.yieldengine.uilib.projectmng;

import com.formdev.flatlaf.icons.FlatMenuArrowIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.xebisco.yieldengine.uilib.*;
import com.xebisco.yieldengine.uilib.Settings.Workspace;
import com.xebisco.yieldengine.utils.ArrayUtils;
import com.xebisco.yieldengine.utils.ColorPalette;
import com.xebisco.yieldengine.utils.FileUtils;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProjectMng extends OptionsFrame {
    private JList<Project> projectsJList;
    private final Class<? extends ProjectEditor> projectEditorClass;

    public ProjectMng(Class<? extends ProjectEditor> projectEditorClass) {
        this.projectEditorClass = projectEditorClass;
    }

    public final static ColorPalette PROJECT_PALETTE = new ColorPalette(null, "project");

    static {
        PROJECT_PALETTE.putFromStandard(ColorPalette.Colors.DARK_BLUE);
        PROJECT_PALETTE.putFromStandard(ColorPalette.Colors.DARK_ORANGE);
        PROJECT_PALETTE.putFromStandard(ColorPalette.Colors.DARK_RED);
        PROJECT_PALETTE.putFromStandard(ColorPalette.Colors.DARK_VIOLET);
    }

    @Override
    public String title() {
        return "Projects Manager";
    }

    private String filter = "";

    private void reload() {
        if (projectsJList == null) {
            projectsJList = new JList<>();
            projectsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            projectsJList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            projectsJList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    new FlatMenuArrowIcon().paintIcon(this, g, getWidth() - 20, getHeight() / 2 - 8 / 2);
                }

                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    c.setText(((Project) value).getName());
                    c.setBorder(new FlatLineBorder(new Insets(10, 10, 10, 10), 25));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                    c.setIconTextGap(8);
                    c.setIcon(new ProjectIcon((Project) value));
                    c.setPreferredSize(new Dimension(60, 60));
                    return c;
                }
            });
            projectsJList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Project p;
                    try {
                        p = projectsJList.getModel().getElementAt((int) Math.floor(e.getY() / 60f));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        return;
                    }

                    projectsJList.setSelectedValue(p, false);

                    JPopupMenu popupMenu = new JPopupMenu();

                    popupMenu.add(new AbstractAction("Open") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Object editor = projectEditorClass.getDeclaredConstructor(Project.class).newInstance(p);
                                dispose();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                     NoSuchMethodException ex) {
                                UIUtils.error(ex, ProjectMng.this);
                            }
                        }
                    });

                    popupMenu.add(new AbstractAction("Remove") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Settings.INSTANCE.workspace.setProjects(ArrayUtils.remove(Settings.INSTANCE.workspace.getProjects(), p));
                            reload();
                        }
                    });

                    popupMenu.show(projectsJList, projectsJList.getWidth() - 20 - 10, projectsJList.getSelectedIndex() * 60 + 60 / 2 - 10);
                }
            });
        }
        if (filter == null) filter = "";
        projectsJList.setListData(Stream.of(Settings.INSTANCE.workspace.getProjects()).filter(p -> p.getName().toUpperCase().contains(filter.toUpperCase())).toArray(Project[]::new));

        SwingUtilities.invokeLater(() -> ((JViewport) projectsJList.getParent()).updateUI());
    }

    public void addProject(Project project, File projectDir) {
        File projectFile = new File(projectDir, "project.ser");
        try {
            //noinspection ResultOfMethodCallIgnored
            projectDir.mkdirs();
            if (projectFile.createNewFile()) {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(projectFile))) {
                    oos.writeObject(project);
                }
            } else if (!(projectFile.exists() && projectFile.isFile())) {
                UIUtils.error(new IOException("Project file already exists. " + projectFile.getAbsolutePath()), this);
            }
            project.setProjectPath(projectFile);
            project.setProjectDir(projectDir);
            Settings.INSTANCE.workspace.setProjects(ArrayUtils.insertFirst(Settings.INSTANCE.workspace.getProjects(), project));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProject(File projectFile) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(projectFile))) {
            addProject((Project) ois.readObject(), projectFile.getParentFile());
        } catch (IOException | ClassNotFoundException e) {
            UIUtils.error(e, ProjectMng.this);
        }
    }

    @Override
    public Pair<Runnable, DefaultMutableTreeNode[]> tabs() {
        List<DefaultMutableTreeNode> nodes = new ArrayList<>();

        JPanel projectListPanel = new JPanel(new BorderLayout());
        DefaultMutableTreeNode projectList = new DefaultMutableTreeNode(projectListPanel, false);
        nodes.add(projectList);
        projectListPanel.setName("Projects");
        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);

        SearchBar<Project> projectsSearchBar = new SearchBar<>();
        projectsSearchBar.getSearchBarListenerList().add(() -> {
            filter = projectsSearchBar.getText();
            reload();
        });
        projectsSearchBar.setOpaque(false);
        toolBar.add(projectsSearchBar);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(new AbstractAction("New Project") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(ProjectMng.this, "New Project", true);
                Project project = new Project();
                dialog.add(UIUtils.getObjectsFieldsPanel(new Object[]{project}, false, () -> {
                    File projectDir = new File(Settings.INSTANCE.workspace.getDirectory(), project.getName());
                    addProject(project, projectDir);
                    reload();
                }, dialog::dispose).second());
                dialog.pack();
                dialog.setLocationRelativeTo(ProjectMng.this);
                dialog.setVisible(true);
            }
        });
        toolBar.add(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenProject openProject = UIUtils.openDialog(new OpenProject().setProjectFile(Settings.INSTANCE.workspace.getDirectory()), ProjectMng.this);
                    addProject(openProject.getProjectFile());
                    reload();
            }
        });
        toolBar.add(new AbstractAction("Settings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.INSTANCE.openSettings(ProjectMng.this);
            }
        });
        toolBar.add(new AbstractAction("Reload") {
            @Override
            public void actionPerformed(ActionEvent e) {
                reload();
            }
        });

        projectListPanel.add(toolBar, BorderLayout.NORTH);

        reload();
        projectsJList.setBackground(getBackground());
        JScrollPane projectsScroll = new JScrollPane(projectsJList);
        projectsScroll.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        projectsScroll.getVerticalScrollBar().setUnitIncrement(16);
        projectsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        projectListPanel.add(projectsScroll, BorderLayout.CENTER);

        //UIUtils.depthPanel(projectsNode, "Projects");


        JPanel optionsPanel = new JPanel();
        DefaultMutableTreeNode optionsNode = new DefaultMutableTreeNode(optionsPanel, false);
        optionsPanel.setName("Options");
        nodes.add(optionsNode);

        return new Pair<>(null, nodes.toArray(new DefaultMutableTreeNode[0]));
    }

    @Override
    public Runnable help() {
        return () -> {

        };
    }
}
