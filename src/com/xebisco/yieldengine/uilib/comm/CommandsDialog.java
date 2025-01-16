package com.xebisco.yieldengine.uilib.comm;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.xebisco.yieldengine.uilib.DocumentChangeListener;
import com.xebisco.yieldengine.uilib.SearchBar;
import com.xebisco.yieldengine.uilib.Settings;
import com.xebisco.yieldengine.uilib.TextPrompt;
import com.xebisco.yieldengine.uilib.projectmng.Project;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CommandsDialog extends JDialog {
    private static final List<MappedCommand> COMMANDS = new ArrayList<>();

    public static void putCommand(Command command) {
        String tab = command.tab();
        if (tab.isEmpty()) {
            tab = Thread.currentThread().getStackTrace()[2].getClassName();
        }
        COMMANDS.add(new MappedCommand(tab, command));
    }

    record MappedCommand(String tab, Command command) {
    }

    private final JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

    public CommandsDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                dispose();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        setUndecorated(true);
        setSize(500, 400);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        getRootPane().setOpaque(false);
        getContentPane().setBackground(new Color(0, 0, 0, 0));
        setBackground(new Color(0, 0, 0, 0));

        SearchBar<String> mainSearchBar = new SearchBar<>();
        mainPanel.add(mainSearchBar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        JList<MappedCommand> commandJList = new JList<>(COMMANDS.toArray(new MappedCommand[0]));
        mainSearchBar.getSearchBarListenerList().add(() -> {
            commandJList.setListData(COMMANDS.stream().filter(p -> p.command().name().toUpperCase().contains(mainSearchBar.getText().toUpperCase())).toArray(MappedCommand[]::new));
        });
        commandJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setText(((MappedCommand) value).tab() + ": " + ((MappedCommand) value).command.name());

                    c.setPreferredSize(new Dimension(30, 30));

                return c;
            }
        });
        commandJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && commandJList.getSelectedValue() != null) {
                    selectCommand(commandJList.getSelectedValue());
                }
            }
        });
        scrollPane.setViewportView(commandJList);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(new FlatRoundBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.setOpaque(false);
        add(mainPanel);

        setVisible(true);
        requestFocus();
        SwingUtilities.invokeLater(mainSearchBar::requestFocus);
    }

    private void selectCommand(MappedCommand command) {
        mainPanel.removeAll();

        JTextField arguments = new JTextField();
        TextPrompt prompt = new TextPrompt(command.tab() + ": " + command.command().name(), arguments);
        prompt.setShow(TextPrompt.Show.ALWAYS);

        mainPanel.add(arguments, BorderLayout.NORTH);

        JPanel centerP = new JPanel(new BorderLayout());
        centerP.setOpaque(false);
        mainPanel.add(centerP, BorderLayout.CENTER);

        JPanel argumentsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        argumentsPanel.setOpaque(false);

        JScrollPane argsScrollPane = new JScrollPane() {
            @Override
            public JScrollBar createHorizontalScrollBar() {
                JScrollBar horizontal = new JScrollPane.ScrollBar(Adjustable.HORIZONTAL);
                horizontal.setPreferredSize(new Dimension(0, 0));
                return horizontal;
            }
        };
        argsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        argsScrollPane.setBorder(new FlatRoundBorder());
        argsScrollPane.setOpaque(false);

        arguments.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dispose();
                    String[] ls = arguments.getText().split("\\s");
                    List<String> list = new ArrayList<>();
                    for (String s : ls) {
                        if (!s.isBlank()) list.add(s);
                    }
                    command.command.run(list.toArray(new String[0]));
                }
            }
        });

        Command.Argument[] args = command.command().args();
        if (args != null) {
            JLabel[] argumentsLabels = new JLabel[args.length];

            for (int i = 0; i < args.length; i++) {
                Command.Argument arg = args[i];
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setText(" " + arg.argument() + ": " + arg.type().getSimpleName() + " ");
                label.setBorder(new FlatRoundBorder());
                argumentsLabels[i] = label;
                argumentsPanel.add(label);
            }
            argsScrollPane.setViewportView(argumentsPanel);
            centerP.add(argsScrollPane, BorderLayout.NORTH);

            Color bkg = new JLabel().getForeground();

            arguments.getDocument().addDocumentListener((DocumentChangeListener) e -> {
                String[] ls = arguments.getText().split("\\s");
                List<String> list = new ArrayList<>();
                for (String s : ls) {
                    if (!s.isBlank()) list.add(s);
                }
                int size = list.size();
                if (arguments.getText().endsWith(" ")) {
                    size++;
                }

                for (JLabel label : argumentsLabels) {
                    label.setForeground(bkg);
                }

                if (!list.isEmpty() && size <= argumentsLabels.length) {
                    JLabel a = argumentsLabels[size - 1];
                    a.setForeground(UIManager.getColor("List.selectionBackground"));
                    argumentsPanel.scrollRectToVisible(a.getBounds());
                }
                repaint();
            });

            argumentsPanel.scrollRectToVisible(new Rectangle(5, 5, 64, 18));
        }


        mainPanel.updateUI();
    }

}
