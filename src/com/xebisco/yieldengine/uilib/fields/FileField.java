package com.xebisco.yieldengine.uilib.fields;

import com.formdev.flatlaf.icons.*;
import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.xebisco.yieldengine.uilib.DocumentChangeListener;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.FileExtensions;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class FileField extends EditableField {
    private final JTextField textField;

    public FileField(String name, File value, FileExtensions extensions, boolean editable) {
        setLayout(new BorderLayout(5, 0));
        add(UIUtils.nameLabel(name), BorderLayout.WEST);

        JPanel valuePanel = new JPanel(new BorderLayout());
        textField = new JTextField(value == null ? "" : value.getPath());
        FlatRoundBorder border = new FlatRoundBorder();
        textField.setBorder(border);
        updateBorder(getValue().exists(), border);
        textField.setEditable(editable);
        valuePanel.add(textField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton viewButton = new JButton(new AbstractAction("", new FlatFileViewComputerIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] f = FileField.this.getValue().getName().split("\\.");
                String ext = f[f.length - 1].toUpperCase();
                if(UIUtils.OPEN_FILE_EXTENSIONS_MAP.containsKey(ext)) {
                    UIUtils.OPEN_FILE_EXTENSIONS_MAP.get(ext).open(FileField.this.getValue(), SwingUtilities.getWindowAncestor(FileField.this));
                } else {
                    try {
                        Desktop.getDesktop().open(FileField.this.getValue());
                    } catch (Throwable ex) {
                        UIUtils.error(ex, FileField.this);
                    }
                }
            }
        });
        viewButton.setEnabled(getValue().exists());
        textField.getDocument().addDocumentListener((DocumentChangeListener) _ -> {
            updateBorder(getValue().exists(), border);
            viewButton.setEnabled(getValue().exists());
        });
        viewButton.setContentAreaFilled(false);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttonPanel.add(viewButton);

        JButton loadButton = new JButton(new AbstractAction("", new FlatFileViewFileIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
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
                if (FileField.this.getValue().exists())
                    fileChooser.setCurrentDirectory(FileField.this.getValue());
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    textField.setText(file.getPath());
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

    @Override
    public File getValue() {
        return new File(textField.getText());
    }
}
