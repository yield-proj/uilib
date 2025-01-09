package com.xebisco.yieldengine.uilib.projectmng;

import com.xebisco.yieldengine.utils.Color4f;
import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;

import java.io.File;
import java.io.Serializable;

public class Project implements Serializable {
    public static final long serialVersionUID = 1511904952538922660L;
    @Visible
    @Editable
    private String name = "";
    private Color4f showColor = ProjectMng.PROJECT_PALETTE.getRandomColor();

    private transient File projectPath, projectDir;

    public String getName() {
        return name;
    }

    public Project setName(String name) {
        this.name = name;
        return this;
    }

    public Color4f getShowColor() {
        return showColor;
    }

    public Project setShowColor(Color4f showColor) {
        this.showColor = showColor;
        return this;
    }

    public File getProjectPath() {
        return projectPath;
    }

    protected Project setProjectPath(File projectPath) {
        this.projectPath = projectPath;
        return this;
    }

    public File getProjectDir() {
        return projectDir;
    }

    protected Project setProjectDir(File projectDir) {
        this.projectDir = projectDir;
        return this;
    }
}
