import com.xebisco.yieldengine.uilib.ProjectEditor;
import com.xebisco.yieldengine.uilib.BasicSettings;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.projectmng.Project;
import com.xebisco.yieldengine.uilib.projectmng.ProjectMng;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ProjectTest {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        UIUtils.setupLaf();

        SwingUtilities.invokeAndWait(() -> {
            SettingsWindow.APP_NAME = "uitest";
            SettingsWindow.INSTANCE = new BasicSettings();
            SettingsWindow.loadSettings(BasicSettings.class);
            ProjectMng mng = new ProjectMng(ProjectEditor.class, Project.class);
            mng.setLocationRelativeTo(null);
            mng.setVisible(true);
        });

    }
}
