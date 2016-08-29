package com.avvero.thingstorage.utils;

import com.avvero.thingstorage.exception.ThingStorageException;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author fxdev-belyaev-ay
 */
    public class ImageUtils {

    /**
     * Takes a file, and resizes it to the given width and height, while keeping
     * original proportions. Note: It resizes a new file rather than resizing
     * the original one. Resulting file is always written as a png file due to issues
     * with resizing jpeg files which results in color loss. See:
     * http://stackoverflow.com/a/19654452/49153
     * for details, including the comments.
     */
    public static void resizeThroughScalr(String sourceDir, String destinationDir, String fileName, String ext,
                                    int width, int height, String cacheMethod) {
        try {
            Image image = ImageIO.read(new File(sourceDir, String.format("%s.%s", fileName, ext)));
            loadCompletely(image);
            BufferedImage bm = toBufferedImage(image);
            // Через простой алгоритм
            bm = Scalr.resize(bm, Scalr.Method.valueOf(cacheMethod), width, height);
            File outFile = new File(destinationDir, String.format("%s.%s", fileName, ext));
            outFile.getParentFile().mkdirs(); //создадим все директории
            ImageIO.write(bm, ext, outFile);
        } catch (IOException e) {
            throw new ThingStorageException(e);
        }
    }

    /**
     * Сложный механизм
     *
     * @param img
     * @return
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        bimage.getGraphics().drawImage(img, 0, 0, null);
        bimage.getGraphics().dispose();
        return bimage;
    }

    /**
     * Since some methods like toolkit.getImage() are asynchronous, this
     * method should be called to load them completely.
     */
    public static void loadCompletely(Image img) {
        MediaTracker tracker = new MediaTracker(new JPanel());
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void resizeImage(String sourceDir, String destinationDir, String fileName, String ext,
                                   int width, int height) {
        try {
            Image image = ImageIO.read(new File(sourceDir, String.format("%s.%s", fileName, ext)));
            int originalWidth = image.getWidth((img, infoflags, x, y, width1, height1) -> true);
            int originalHeight = image.getHeight((img, infoflags, x, y, width1, height1) -> true);

            float scaleX = (float) width / originalWidth;
            float scaleY = (float) height / originalHeight;
            float scale = Math.min(scaleX, scaleY);
            int w = Math.round(originalWidth * scale);
            int h = Math.round(originalHeight * scale);

            BufferedImage temp = resizeImage(image, w, h);
            File outFile = new File(destinationDir, String.format("%s.%s", fileName, ext));
            outFile.getParentFile().mkdirs(); //создадим все директории
            ImageIO.write(temp, ext, outFile);
        } catch (IOException e) {
            throw new ThingStorageException(e);
        }
    }

    public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}