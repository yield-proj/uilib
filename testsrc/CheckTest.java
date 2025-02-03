import com.xebisco.yieldengine.uilib.BasicSettings;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.comm.Command;
import com.xebisco.yieldengine.uilib.comm.CommandsDialog;
import com.xebisco.yieldengine.uilib.fields.MultiComboField;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class CheckTest {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        UIUtils.setupLaf();

        SwingUtilities.invokeAndWait(() -> {
            SettingsWindow.APP_NAME = "uitest";
            SettingsWindow.INSTANCE = new BasicSettings();
            SettingsWindow.loadSettings(BasicSettings.class);
            JFrame frame = new JFrame();
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel p = new JPanel();

            try {
                p.add(new MultiComboField("test", new String[]{"a"}, CheckTest.class.getMethod("opt"), true));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            frame.add(p);

            frame.setVisible(true);
        });
    }

    public static String[] opt() {
        return new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    }
}
