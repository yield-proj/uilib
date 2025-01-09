package com.xebisco.yieldengine.uilib;

import com.xebisco.yieldengine.uilib.projectmng.Project;

import javax.swing.*;
import java.awt.*;

public class ProjectIcon implements Icon {

    private Dimension size = new Dimension(30, 30);
    private final Project project;

    public ProjectIcon(Project project) {
        this.project = project;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font f = g.getFont();
        g.setColor(UIUtils.getColor(project.getShowColor()));
        g.fillRoundRect(x, y, size.width, size.height, 10, 10);
        String n = String.valueOf((project.getName() == null) || (project.getName().isEmpty()) ? "" : project.getName().charAt(0));
        g.setFont(g.getFont().deriveFont(Font.BOLD).deriveFont(size.height - 10f));
        g.setColor(Color.WHITE);
        ((Graphics2D) g).drawString(n, x + getIconWidth() / 2f - g.getFontMetrics().stringWidth(n) / 2f, y + getIconHeight() / 2f + g.getFontMetrics().getHeight() / 4f);
        g.setFont(f);
    }

    @Override
    public int getIconWidth() {
        return size.width;
    }

    @Override
    public int getIconHeight() {
        return size.height;
    }

    public Dimension getSize() {
        return size;
    }

    public ProjectIcon setSize(Dimension size) {
        this.size = size;
        return this;
    }

    public Project getProject() {
        return project;
    }
}
