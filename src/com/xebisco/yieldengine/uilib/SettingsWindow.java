package com.xebisco.yieldengine.uilib;

import com.xebisco.yieldengine.uilib.projectmng.OptionsFrame;
import com.xebisco.yieldengine.utils.FileUtils;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class SettingsWindow extends OptionsFrame {
    public static BasicSettings INSTANCE;
    public static String APP_NAME;

    public static void loadSettings(Class<? extends BasicSettings> settingsClass) {
        if (INSTANCE == null) {
            throw new IllegalStateException("Settings INSTANCE is null");
        }
        File dir = new File(FileUtils.getWorkingDirectory(), APP_NAME);
        //noinspection ResultOfMethodCallIgnored
        dir.mkdir();
        File settingsFile = new File(dir, "yield_settings.ser");
        if (settingsFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settingsFile))) {
                INSTANCE = (BasicSettings) ois.readObject();
            } catch (Exception e) {
                UIUtils.error(e);
                System.exit(12);
            }
        } else {
            try {
                INSTANCE = settingsClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                UIUtils.error(e);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settingsFile))) {
                oos.writeObject(INSTANCE);
            } catch (IOException e) {
                UIUtils.error(e);
            }
        }, "SaveSettings"));
    }

    public static void openSettings(Frame frame) {
        JFrame f = new SettingsWindow();
        f.setLocationRelativeTo(frame);

        f.setVisible(true);
    }

    @Override
    public String title() {
        return "Settings";
    }

    @Override
    public Pair<Runnable, DefaultMutableTreeNode[]> tabs() {
        Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs = INSTANCE.tabs();
        return new Pair<>(() -> tabs.first().forEach(Runnable::run), tabs.second().toArray(new DefaultMutableTreeNode[0]));
    }

    @Override
    public Runnable help() {
        return INSTANCE.help();
    }
}