package com.xebisco.yieldengine.uilib;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.SystemInfo;
import com.xebisco.yieldengine.uilib.fields.*;
import com.xebisco.yieldengine.uilib.theme.DarkerLaf;
import com.xebisco.yieldengine.utils.*;
import gui.ImageEditor;
import org.joml.Vector2f;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class UIUtils {
    private static final List<Component> pafComponents = new ArrayList<>();
    public static final Map<Class<? extends Serializable>, ReturnField> RETURN_FIELD_MAP = new HashMap<>();
    public static final Map<String, OpenFile> OPEN_FILE_EXTENSIONS_MAP = new HashMap<>();

    public interface OpenFile {
        void open(File file, Window window);
    }

    static {
        RETURN_FIELD_MAP.put(String.class, ((name, value, editable, field) -> {
            if (field.isAnnotationPresent(ComboString.class)) {
                try {
                    return new ComboField(name, (String) value, field.getAnnotation(ComboString.class).type().getMethod(field.getAnnotation(ComboString.class).method()), editable);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return new StringField(name, (String) value, editable);
            }
        }));
        RETURN_FIELD_MAP.put(Color4f.class, ((name, value, editable, _) -> new ColorField(name, (Color4f) value, editable)));
        RETURN_FIELD_MAP.put(File.class, ((name, value, editable, field) -> new FileField(name, (File) value, field.getAnnotation(FileExtensions.class), editable)));

        RETURN_FIELD_MAP.put(Byte.class, ((name, value, editable, _) -> new NumberField<>(name, (Byte) value, Byte.class, editable)));
        RETURN_FIELD_MAP.put(byte.class, ((name, value, editable, _) -> new NumberField<>(name, (byte) value, Byte.class, editable)));
        RETURN_FIELD_MAP.put(Short.class, ((name, value, editable, _) -> new NumberField<>(name, (Short) value, Short.class, editable)));
        RETURN_FIELD_MAP.put(short.class, ((name, value, editable, _) -> new NumberField<>(name, (short) value, Short.class, editable)));
        RETURN_FIELD_MAP.put(Integer.class, ((name, value, editable, _) -> new NumberField<>(name, (Integer) value, Integer.class, editable)));
        RETURN_FIELD_MAP.put(int.class, ((name, value, editable, _) -> new NumberField<>(name, (Integer) value, int.class, editable)));
        RETURN_FIELD_MAP.put(Long.class, ((name, value, editable, _) -> new NumberField<>(name, (Long) value, Long.class, editable)));
        RETURN_FIELD_MAP.put(long.class, ((name, value, editable, _) -> new NumberField<>(name, (long) value, Long.class, editable)));
        RETURN_FIELD_MAP.put(Float.class, ((name, value, editable, _) -> new NumberField<>(name, (Float) value, Float.class, editable)));
        RETURN_FIELD_MAP.put(float.class, ((name, value, editable, _) -> new NumberField<>(name, (float) value, Float.class, editable)));
        RETURN_FIELD_MAP.put(Double.class, ((name, value, editable, _) -> new NumberField<>(name, (Double) value, Double.class, editable)));
        RETURN_FIELD_MAP.put(double.class, ((name, value, editable, _) -> new NumberField<>(name, (double) value, Double.class, editable)));

        RETURN_FIELD_MAP.put(Boolean.class, ((name, value, editable, _) -> new BooleanField(name, (Boolean) value, editable)));
        RETURN_FIELD_MAP.put(boolean.class, ((name, value, editable, _) -> new BooleanField(name, (boolean) value, editable)));

        RETURN_FIELD_MAP.put(Vector2f.class, ((name, value, editable, _) -> new Vector2Field(name, (Vector2f) value, editable)));

        OpenFile openImage = (file, window) -> new ImageEditor(window, file).setVisible(true);

        OPEN_FILE_EXTENSIONS_MAP.put("PNG", openImage);
        OPEN_FILE_EXTENSIONS_MAP.put("JPEG", openImage);
        OPEN_FILE_EXTENSIONS_MAP.put("JPG", openImage);
        OPEN_FILE_EXTENSIONS_MAP.put("GIF", openImage);
        OPEN_FILE_EXTENSIONS_MAP.put("TIFF", openImage);
        OPEN_FILE_EXTENSIONS_MAP.put("WBMP", openImage);
        OPEN_FILE_EXTENSIONS_MAP.put("BMP", openImage);
    }

    public static void setupLaf() {
        if (SystemInfo.isLinux) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        DarkerLaf.setup();

        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);

    }

    public static BufferedImage resizeImage(BufferedImage image, int resize) {
        int w = resize, h = resize;
        if (image.getWidth() > image.getHeight()) {
            h = -1;
        } else {
            w = -1;
        }

        Image resized = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);

        BufferedImage result = new BufferedImage(resized.getWidth(null), resized.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();
        g.drawImage(resized, 0, 0, null);
        g.dispose();
        return result;
    }

    public static ArrayList<Pair<String, EditableField>> getFields(Object object) {
        ArrayList<Pair<String, EditableField>> ret = new ArrayList<>();
        ArrayList<Pair<String, Pair<Serializable, Field>>> fields = ObjectUtils.get(object);
        for (Pair<String, Pair<Serializable, Field>> field : fields) {

            if (field.second().second().getType().isArray()) {
                if (field.second().second().isAnnotationPresent(ComboString.class)) {
                    try {
                        ret.add(new Pair<>(field.first(), new MultiComboField(field.second().second().getName(), (String[]) field.second().first(), field.second().second().getAnnotation(ComboString.class).type().getMethod(field.second().second().getAnnotation(ComboString.class).method()), true)));
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    ret.add(new Pair<>(field.first(), new ArrayField(field.second().second(), (Object[]) field.second().first(), field.second().second().getType().getComponentType(), field.second().second().isAnnotationPresent(Editable.class))));
                }
            } else {
                try {
                    ret.add(new Pair<>(field.first(), RETURN_FIELD_MAP.get(field.second().second().getType()).getEditableField(field.second().second().getName(), field.second().first(), field.second().second().isAnnotationPresent(Editable.class), field.second().second())));
                } catch (NullPointerException e) {
                    ret.add(new Pair<>(field.first(), new ObjectField(field.first(), field.second().first())));
                /*
                e.printStackTrace();
                    Logger.debug(field.second().second().getType() + " does not have an EditableField class.");

                 */
                }
            }

        }
        processPaf();

        return ret;
    }

    public static EditableField getArrayField(Object object, Field arrayField) {
        EditableField field = null;
        if (arrayField.getType().getComponentType().isArray()) {
            System.err.println("No support for multi-dimensional arrays.");
        } else {
            try {
                Field f = arrayField;
                if (arrayField.getType().isArray())
                    f = null;
                field = RETURN_FIELD_MAP.get(arrayField.getType().getComponentType()).getEditableField(f == null ? null : f.getName(), (Serializable) object, f == null || f.isAnnotationPresent(Editable.class), arrayField);
            } catch (NullPointerException e) {
                field = new ObjectField(null, (Serializable) object);
                /*
                Logger.debug(object.getClass() + " does not have an EditableField class.");

                 */
            }
        }
        processPaf();

        return field;
    }

    public static void depthPanel(DefaultMutableTreeNode main, String name) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        panel.setName(name);
        main.setUserObject(panel);
        panel.setName(getTreeTitle(main));

        for (int i = 0; i < main.getChildCount(); i++) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) main.getChildAt(i);
            JButton button = new JButton(new AbstractAction(((Component) n.getUserObject()).getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTree tree = ((JTree) ((DefaultMutableTreeNode) main.getRoot()).getUserObject());
                    SwingUtilities.invokeLater(() -> tree.setSelectionPath(new TreePath(n.getPath())));
                }
            }) {
                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
                }
            };
            button.setForeground(UIManager.getColor("List.selectionBackground"));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            panel.add(button);
        }
    }

    public static void forAllChildren(DefaultMutableTreeNode root, Consumer<DefaultMutableTreeNode> action) {
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            action.accept(child);
            forAllChildren(child, action);
        }
    }

    public static void addSearchToTree(SearchBar<String> searchBar, JTree tree, DefaultMutableTreeNode root, FilteredListModel.Filter<String> filter) {
        Set<DefaultMutableTreeNode> openNodes = new HashSet<>();
        Set<DefaultMutableTreeNode> savedOpen = new HashSet<>();
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                openNodes.add((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                openNodes.remove((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
            }
        });
        searchBar.getSearchBarListenerList().add(() -> {
            ((DefaultTreeModel) tree.getModel()).nodeStructureChanged((TreeNode) tree.getModel().getRoot());
            if (searchBar.getText().isEmpty()) {
                UIUtils.forAllChildren(root, c -> {
                    if (savedOpen.contains(c))
                        tree.fireTreeExpanded(new TreePath(c.getPath()));
                    else tree.fireTreeCollapsed(new TreePath(c.getPath()));
                });
                savedOpen.clear();
            } else {
                savedOpen.clear();
                savedOpen.addAll(openNodes);
                UIUtils.forAllChildren(root, c -> {
                    tree.fireTreeExpanded(new TreePath(c.getPath()));
                });
            }
        });
    }

    public static boolean shouldHide(DefaultMutableTreeNode node, FilteredListModel.Filter<String> filter) {
        return node.getUserObject() instanceof JTree || (!filter.accept(((Component) node.getUserObject()).getName()) && node.isLeaf());
    }

    public static String getTreeTitle(DefaultMutableTreeNode node) {
        StringBuilder title = new StringBuilder();
        for (TreeNode p : node.getPath()) {
            if (((DefaultMutableTreeNode) p).getUserObject() instanceof JTree) continue;
            title.append(((Component) ((DefaultMutableTreeNode) p).getUserObject()).getName()).append(" > ");
        }
        if (title.toString().endsWith(" > ")) title.setLength(title.length() - 3);
        return title.toString();
    }

    public static Color getColor(Color4f c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    public static Pair<Runnable, JPanel> getFieldsPanel(Object object, boolean showApplyButton, Runnable applyRunnable, Runnable closeRunnable) {
        return getFieldsPanel(getFields(object), showApplyButton, applyRunnable, object, closeRunnable);
    }

    @FunctionalInterface
    public interface ArrayChange {
        void apply(int from, int to);
    }

    public static JLabel nameLabel(String name) {
        JLabel label = new JLabel();
        if (name == null) label.setText(null);
        else {
            label.setText(prettyString(name) + ":");
            addPaf(label);
        }
        return label;
    }

    public static String prettyString(String s) {
        if (s.isEmpty()) return s;
        StringBuilder builder = new StringBuilder().append(Character.toUpperCase(s.charAt(0)));
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!Character.isUpperCase(s.charAt(i - 1)) && !Character.isWhitespace(s.charAt(i - 1))) {
                    builder.append(" ");
                }
            }
            builder.append(c);
        }
        return builder.toString();
    }

    public static Pair<Runnable, JPanel> getObjectsFieldsPanel(Object[] objects, boolean showApplyButton, Runnable applyRunnable, Runnable closeRunnable, ArrayChange arrayChange, Field arrayField) {
        JPanel out = new JPanel(new BorderLayout());

        JPanel groups = new JPanel();
        groups.setLayout(new BoxLayout(groups, BoxLayout.Y_AXIS));

        List<Runnable> applyList = new ArrayList<>();

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            JPanel panel = new JPanel(new BorderLayout()) {
                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
                }
            };
            int finalI = i;
            Pair<Runnable, JPanel> obj;
            EditableField field;
            if (arrayField != null) {
                field = getArrayField(object, arrayField);
                obj = getFieldsPanel(List.of(new Pair<>(null, field)), false, null, object, null);
                applyList.add(() -> objects[finalI] = field.getValue());
            } else {
                field = null;
                obj = getFieldsPanel(getFields(object), false, null, object, null);
                applyList.add(obj.first());
            }

            /*if (object.getClass().isAnnotationPresent(Group.class)) {
                JLabel groupTitle = new JLabel(object.getClass().getAnnotation(Group.class).name());
                groupTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                JPanel title = new JPanel(new BorderLayout());
                title.add(groupTitle, BorderLayout.WEST);
                JPanel separatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(getForeground());
                        g.drawLine(10, getHeight() / 2, getWidth() - 10, getHeight() / 2);
                    }
                };
                title.add(separatorPanel, BorderLayout.CENTER);
                panel.add(title, BorderLayout.NORTH);
                obj.second().setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
            }
             */
            panel.add(obj.second(), BorderLayout.CENTER);
            if (arrayField != null) {
                JToolBar arrayToolBar = new JToolBar();
                arrayToolBar.setFloatable(false);
                arrayToolBar.setRollover(true);
                boolean alt = arrayField.isAnnotationPresent(AltArray.class);
                if (alt) {
                    arrayToolBar.add(nameLabel(object.getClass().getSimpleName()));
                    arrayToolBar.add(Box.createHorizontalGlue());
                    arrayToolBar.setBorder(new FlatLineBorder(new Insets(0, 3, 0, 3), UIManager.getColor("Component.borderColor"), 100, 8));
                }
                arrayToolBar.add(new AbstractAction(alt ? "More" : i + ":") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem item = new JMenuItem(new AbstractAction("Move Up") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                arrayChange.apply(finalI, finalI - 1);
                            }
                        });
                        popupMenu.add(item);
                        item.setEnabled(finalI > 0);
                        item = new JMenuItem(new AbstractAction("Move Down") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                arrayChange.apply(finalI, finalI + 1);
                            }
                        });
                        popupMenu.add(item);
                        item.setEnabled(finalI < objects.length - 1);
                        popupMenu.addSeparator();
                        item = new JMenuItem(new AbstractAction("Remove") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                arrayChange.apply(finalI, -1);
                            }
                        });
                        popupMenu.add(item);
                        if (alt) {
                            popupMenu.show(arrayToolBar, arrayToolBar.getWidth() - popupMenu.getPreferredSize().width, arrayToolBar.getHeight());
                        } else {
                            popupMenu.show(arrayToolBar, 0, arrayToolBar.getHeight());
                        }
                    }
                });
                if (alt)
                    panel.add(arrayToolBar, BorderLayout.NORTH);
                else
                    panel.add(arrayToolBar, BorderLayout.WEST);
            }
            groups.add(panel);
        }

        out.add(groups);

        if (!(applyRunnable == null && closeRunnable == null)) {

            groups.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            out.add(getButtonPanel(showApplyButton, () -> {
                applyList.forEach(Runnable::run);
                if (applyRunnable != null)
                    applyRunnable.run();
            }, closeRunnable), BorderLayout.SOUTH);
        }

        return new Pair<>(() -> applyList.forEach(Runnable::run), out);
    }

    public static Pair<Runnable, JPanel> getObjectsFieldsPanel(Object[] objects, boolean showApplyButton, Runnable applyRunnable, Runnable closeRunnable) {
        return getObjectsFieldsPanel(objects, showApplyButton, applyRunnable, closeRunnable, null, null);
    }

    public static Pair<Runnable, JPanel> getObjectsFieldsPanel(Object[] objects) {
        return getObjectsFieldsPanel(objects, false, null, null);
    }

    public static JPanel getButtonPanel(boolean showApplyButton, Runnable applyRunnable, Runnable closeRunnable) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyRunnable.run();
                if (closeRunnable != null) closeRunnable.run();
            }
        });
        buttonPanel.add(okButton);
        SwingUtilities.invokeLater(() -> buttonPanel.getRootPane().setDefaultButton(okButton));

        if (closeRunnable != null) {
            buttonPanel.add(new JButton(new AbstractAction("Cancel") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    closeRunnable.run();
                }
            }));
            if (showApplyButton) {
                buttonPanel.add(new JButton(new AbstractAction("Apply") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        applyRunnable.run();
                    }
                }));
            }
        }

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        return buttonPanel;
    }

    public static Pair<Runnable, JPanel> getFieldsPanel(List<Pair<String, EditableField>> fields, boolean showApplyButton, Runnable applyRunnable, Object object, Runnable closeRunnable) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel();
        //panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Pair<String, EditableField> field : fields) {
            panel.add(Box.createVerticalStrut(2));
            panel.add(field.second());
            panel.add(Box.createVerticalStrut(2));
        }
        mainPanel.add(panel, BorderLayout.CENTER);
        if (applyRunnable != null) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton okButton = new JButton(new AbstractAction("OK") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    applyFields(object, fields);
                    applyRunnable.run();
                    if (closeRunnable != null) closeRunnable.run();
                }
            });
            buttonPanel.add(okButton);
            SwingUtilities.invokeLater(() -> mainPanel.getRootPane().setDefaultButton(okButton));

            if (closeRunnable != null) {
                buttonPanel.add(new JButton(new AbstractAction("Cancel") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        closeRunnable.run();
                    }
                }));
                if (showApplyButton) {
                    buttonPanel.add(new JButton(new AbstractAction("Apply") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            applyFields(object, fields);
                            applyRunnable.run();
                            closeRunnable.run();
                        }
                    }));
                }
            }

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        return new Pair<>(() -> applyFields(object, fields), mainPanel);
    }

    public static <T> T openDialog(T object, Frame frame) {
        JDialog dialog = new JDialog(frame, prettyString(object.getClass().getSimpleName()), true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setMinimumSize(new Dimension(300, 100));

        dialog.add(getObjectsFieldsPanel(new Object[]{object}, false, () -> {
        }, dialog::dispose).second());
        dialog.pack();

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);

        return object;
    }

    public static void applyFields(Object object, List<Pair<String, EditableField>> fields) {
        Map<String, Serializable> apply = new HashMap<>();
        for (Pair<String, EditableField> field : fields) {
            apply.put(field.first(), field.second().getValue());
        }
        ObjectUtils.apply(apply, object);
    }

    public static void about(Frame frame, String aboutHtml, String appTitle) {
        JDialog dialog = new JDialog(frame, "About", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setMinimumSize(new Dimension(400, 350));

        JLabel title = new JLabel(appTitle);
        try {
            title.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(UIUtils.class.getResource("/icons/logo1.png"))).getScaledInstance(64, 64, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        title.setIconTextGap(10);
        dialog.add(title, BorderLayout.NORTH);

        JLabel label = new JLabel("""
                <html>
                    <p>""" + aboutHtml + """
                </p><p> </p>
                <p> VM:\s""" + System.getProperty("java.vm.name") + """
                </p>
                <p>VM version:\s""" + System.getProperty("java.vm.version") + """
                    </p>
                    <p> </p>
                    <p>
                        Xebisco @2022-2024
                    </p>
                </html>
                """);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dialog.add(label);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.setMnemonic('C');
        closeButton.addActionListener(_ -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(closeButton);
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    public static void addPaf(Component component) {
        pafComponents.add(component);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void processPaf() {
        int width = 0;
        for (Component component : pafComponents) {
            width = Math.max(width, component.getPreferredSize().width);
        }
        for (Component component : pafComponents) {
            //component.setPreferredSize(new Dimension(width, component.getPreferredSize().height));
        }
        pafComponents.clear();
    }

    public static void error(Throwable e, Component parent) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    public static void error(Throwable e) {
        error(e, null);
    }

    public static <E> AbstractListModel<E> arrayToModel(E[] array) {
        return new AbstractListModel<>() {
            public int getSize() {
                return array.length;
            }

            public E getElementAt(int i) {
                return array[i];
            }
        };
    }

    public static <E> AbstractListModel<E> listToModel(List<E> list) {
        return new AbstractListModel<E>() {
            public int getSize() {
                return list.size();
            }

            public E getElementAt(int i) {
                return list.get(i);
            }
        };
    }

    public static void addPrompt(String text, JTextField textField) {
        TextPrompt textPrompt = new TextPrompt(text, textField);
        textPrompt.changeAlpha(0.5f);
        textPrompt.changeStyle(Font.ITALIC);
    }
}
