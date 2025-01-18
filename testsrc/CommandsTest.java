import com.xebisco.yieldengine.uilib.BasicSettings;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.comm.Command;
import com.xebisco.yieldengine.uilib.comm.CommandsDialog;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class CommandsTest {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        UIUtils.setupLaf();

        SwingUtilities.invokeAndWait(() -> {
            SettingsWindow.APP_NAME = "uitest";
            SettingsWindow.INSTANCE = new BasicSettings();
            SettingsWindow.loadSettings(BasicSettings.class);
            CommandsDialog.putCommand(new Command() {
                @Override
                public Argument[] args() {
                    return new Argument[] {
                            new Argument("test", Integer.class)
                    };
                }

                @Override
                public String name() {
                    return "test";
                }

                @Override
                public void run(String[] args) {

                }
            });
            new CommandsDialog();
        });
    }
}
