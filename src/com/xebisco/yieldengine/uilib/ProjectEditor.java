package com.xebisco.yieldengine.uilib;

import com.xebisco.yieldengine.uilib.projectmng.Project;

public class ProjectEditor<P extends Project> {
    private final P project;

    public ProjectEditor(P project) {
        this.project = project;
    }

    public P getProject() {
        return project;
    }
}
