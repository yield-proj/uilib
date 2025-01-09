package com.xebisco.yieldengine.uilib;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    public MainPanel(Component content, String title) {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(14f));
        titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        titleLabel.setVerticalTextPosition(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        titleLabel.setOpaque(false);
        setBackground(content.getBackground());
        //if (!title.isEmpty() && !title.equals("Projects > List"))
            add(titleLabel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    public MainPanel(Component content) {
        this(content, content.getName());
    }
}
