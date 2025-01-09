package com.xebisco.yieldengine.uilib.projectmng;

import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.FileExtensions;
import com.xebisco.yieldengine.utils.Visible;

import java.io.File;

public class OpenProject {
    @Visible
    @Editable
    @FileExtensions(value = {"project.ser"}, name = "Project File")
    private File projectFile;

    public File getProjectFile() {
        return projectFile;
    }

    public OpenProject setProjectFile(File projectFile) {
        this.projectFile = projectFile;
        return this;
    }
}
