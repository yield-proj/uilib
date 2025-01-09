package com.xebisco.yieldengine.uilib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import com.formdev.flatlaf.icons.FlatClearIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;

public class SearchBar<T> extends JPanel {
    private IconTextField textField = new IconTextField();
    private final ArrayList<SearchBarListener> searchBarListenerList = new ArrayList<>();
    private JButton clear = new JButton(new AbstractAction("", new FlatClearIcon()) {
        @Override
        public void actionPerformed(ActionEvent e) {
            textField.setText("");
        }
    });
    private ArrayList<String> searchHistory = new ArrayList<>();

    public interface SearchBarListener {
        void onSearch();
    }

    public SearchBar() {
        setLayout(new BorderLayout());
        UIUtils.addPrompt("Search", textField);
        textField.setIcon(new FlatSearchWithHistoryIcon());
        add(textField, BorderLayout.CENTER);
        textField.setLayout(new BorderLayout());

        clear.setBorder(BorderFactory.createEmptyBorder());
        clear.setContentAreaFilled(false);
        clear.setFocusPainted(false);
        clear.setOpaque(false);

        textField.add(clear, BorderLayout.EAST);

        textField.getDocument().addDocumentListener((DocumentChangeListener) _ -> {
            searchBarListenerList.forEach(SearchBarListener::onSearch);
        });
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getX() > textField.getmHelper().getmBorder().getBorderInsets(textField).left)
                    return;
                JPopupMenu popupMenu = new JPopupMenu();
                for(String s : searchHistory) {
                    popupMenu.add(new AbstractAction(s) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            textField.setText(s);
                        }
                    });
                }
                popupMenu.show(textField, 0, textField.getHeight());
            }
        });

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateSearch();
            }
        });

        textField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSearch();
            }
        });

        /*
        JButton arrowButton = (JButton) comboBox.getComponent(0);
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            arrowButton.doClick();
        });
        add(comboBox, BorderLayout.CENTER);

        comboBox.putClientProperty("JComboBox.", new FlatSearchIcon());

        comboBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateSearch();
            }
        });
        comboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSearch();
            }
        });
        ((JTextField) comboBox.getEditor().getEditorComponent()).getDocument().addDocumentListener((DocumentChangeListener) e -> {
            if (model != null)
                model.doFilter();
            searchBarListenerList.forEach(SearchBarListener::onSearch);
        });
        comboBox.setSelectedItem("");
        comboBox.setEditable(true);

         */
    }

    public IconTextField getTextField() {
        return textField;
    }

    public SearchBar<T> setTextField(IconTextField textField) {
        this.textField = textField;
        return this;
    }

    public JButton getClear() {
        return clear;
    }

    public SearchBar<T> setClear(JButton clear) {
        this.clear = clear;
        return this;
    }

    public ArrayList<String> getSearchHistory() {
        return searchHistory;
    }

    public SearchBar<T> setSearchHistory(ArrayList<String> searchHistory) {
        this.searchHistory = searchHistory;
        return this;
    }

    private void updateSearch() {
        String text = getText();
        if (text == null || text.isEmpty()) return;
        if (!searchHistory.isEmpty()) {
            if (searchHistory.contains(text)) return;
        }
        searchHistory.addFirst(text);
        if (searchHistory.size() > 6) {
            searchHistory.removeLast();
        }
    }


    public String getText() {
        //return ((JTextField) comboBox.getEditor().getEditorComponent()).getText();
        return textField.getText();
    }

    public ArrayList<SearchBarListener> getSearchBarListenerList() {
        return searchBarListenerList;
    }
}
