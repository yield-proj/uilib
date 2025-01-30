package com.xebisco.yieldengine.uilib.fields;

import com.formdev.flatlaf.icons.FlatFileViewComputerIcon;
import com.formdev.flatlaf.icons.FlatFileViewFileIcon;
import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.xebisco.yieldengine.uilib.DirectoryRestrictedFileSystemView;
import com.xebisco.yieldengine.uilib.DocumentChangeListener;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.FileExtensions;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;

public class FileField extends EditableField {
    private final JTextField textField;
    private final DirectoryRestrictedFileSystemView fsv;

    public FileField(String name, File value, FileExtensions extensions, DirectoryRestrictedFileSystemView fsv, boolean editable) {
        this.fsv = fsv;

        setLayout(new BorderLayout(5, 0));
        add(UIUtils.nameLabel(name), BorderLayout.WEST);

        JPanel valuePanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        if(value == null) textField.setText("");
        else if (fsv != null) textField.setText(getPathInDir(value.getPath(), fsv.getRoots()[0]));
        else textField.setText(value.getPath());
        FlatRoundBorder border = new FlatRoundBorder();
        textField.setBorder(border);
        updateBorder(getFileValue().exists(), border);
        textField.setEditable(editable);
        valuePanel.add(textField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton viewButton = new JButton(new AbstractAction("", new FlatFileViewComputerIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] f = FileField.this.getFileValue().getName().split("\\.");
                String ext = f[f.length - 1].toUpperCase();
                if (UIUtils.OPEN_FILE_EXTENSIONS_MAP.containsKey(ext)) {
                    UIUtils.OPEN_FILE_EXTENSIONS_MAP.get(ext).open(FileField.this.getFileValue(), SwingUtilities.getWindowAncestor(FileField.this));
                } else {
                    try {
                        Desktop.getDesktop().open(FileField.this.getFileValue());
                    } catch (Throwable ex) {
                        UIUtils.error(ex, FileField.this);
                    }
                }
            }
        });
        viewButton.setEnabled(getFileValue().exists());
        textField.getDocument().addDocumentListener((DocumentChangeListener) _ -> {
            updateBorder(getFileValue().exists(), border);
            viewButton.setEnabled(getFileValue().exists());
        });
        viewButton.setContentAreaFilled(false);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttonPanel.add(viewButton);

        JButton loadButton = new JButton(new AbstractAction("", new FlatFileViewFileIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser;
                if (fsv != null) fileChooser = new JFileChooser(fsv.getRoots()[0], fsv);
                else fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (extensions != null) {
                    fileChooser.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            if (f.isDirectory()) return true;
                            for (String ext : extensions.value()) {
                                if (f.getName().toUpperCase().endsWith(ext.toUpperCase())) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        @Override
                        public String getDescription() {
                            return extensions.name();
                        }
                    });
                    if (extensions.acceptFiles() && extensions.acceptDirectories())
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    else if (extensions.acceptDirectories())
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    else fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                }
                if (FileField.this.getFileValue().exists())
                    fileChooser.setCurrentDirectory(FileField.this.getFileValue());
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (fsv != null) textField.setText(getPathInDir(file.getPath(), fsv.getRoots()[0]));
                    else textField.setText(file.getPath());
                }
            }
        });
        loadButton.setContentAreaFilled(false);
        loadButton.setFocusPainted(false);
        loadButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        loadButton.setEnabled(editable);
        buttonPanel.add(loadButton);

        valuePanel.add(buttonPanel, BorderLayout.EAST);


        add(valuePanel, BorderLayout.CENTER);
    }

    public static String getPathInDir(String path, File dir) {
        String s = path.replace(dir.getPath(), "");
        if(s.startsWith("/") || s.startsWith("\\")) s = s.substring(1);
        return s;
    }

    public FileField(String name, File value, FileExtensions extensions, boolean editable) {
        this(name, value, extensions, null, editable);
    }

    private String getText() {
        return fsv != null ? new File(fsv.getRoots()[0], textField.getText()).getPath() : textField.getText();
    }

    public void updateBorder(boolean correct, FlatBorder border) {
        if (!correct) {
            border.applyStyleProperty("borderColor", Color.RED);
            border.applyStyleProperty("focusColor", Color.RED);
            border.applyStyleProperty("focusedBorderColor", Color.RED);
        } else {
            border.applyStyleProperty("borderColor", UIManager.getColor("Component.borderColor"));
            border.applyStyleProperty("focusColor", UIManager.getColor("Component.focusColor"));
            border.applyStyleProperty("focusedBorderColor", UIManager.getColor("Component.focusedBorderColor"));
        }
    }

    public File getFileValue() {
        return new File(getText());
    }

    @Override
    public Serializable getValue() {
        return getFileValue();
    }

    public JTextField getTextField() {
        return textField;
    }
}
