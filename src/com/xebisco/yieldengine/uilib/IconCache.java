package com.xebisco.yieldengine.uilib;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IconCache {
    public record SizeIcon(File path, Integer size) {
    }

    private static final Map<SizeIcon, BufferedImage> imageCache = new HashMap<>();

    public static BufferedImage get(SizeIcon icon) {
        return imageCache.get(icon);
    }

    public static BufferedImage dget(SizeIcon icon) {
        if (!imageCache.containsKey(icon)) {
            put(icon.path, icon.size);
        }
        return imageCache.get(icon);
    }

    public static SizeIcon put(File path, Integer size) {
        BufferedImage image;
        try {
            image = ImageIO.read(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (size != null) {
            image = UIUtils.resizeImage(image, size);
        }
        SizeIcon icon = new SizeIcon(path, size);
        imageCache.put(icon, image);
        return icon;
    }
}
