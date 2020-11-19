import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;
import java.util.Timer;

// Класс прорисовки изображения

public class HistoryScanner
{
    private static int i = 0;
    static BufferedImage lastScreanCapture;

    public static double getDifferenceImage(BufferedImage img1, BufferedImage img2) {
        int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for BufferedImage
        int width2 = img2.getWidth(); // take no arguments
        int height1 = img1.getHeight();
        int height2 = img2.getHeight();
        if ((width1 != width2) || (height1 != height2)) {
            System.err.println("Error: Images dimensions mismatch");
            System.exit(1);
        }
        double diff = 0;
        // int result; // Stores output pixel
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                int diffPixel = 0;
                int rgb1 = img1.getRGB(j, i);
                int rgb2 = img2.getRGB(j, i);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;
                diffPixel = Math.abs(r1 - r2); // Change
                diffPixel += Math.abs(g1 - g2);
                diffPixel += Math.abs(b1 - b2);
                diffPixel /= 3; // Change - Ensure result is between 0 - 255
                // Make the difference image gray scale
                // The RGB components are all the same
                // result = (diff << 16) | (diff << 8) | diff;
                // outImg.setRGB(j, i, result); // Set result
                diff += diffPixel / 255.0;
            }
        }
        return (diff * 100) / (height1 * width1);
    }
    public static void main(String[] args) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                
                BufferedImage capture;
                double diff = 0;
                try {
                    // "Вырезаем" часть изображения "рабочего стола"
                    Robot robot = new Robot();
                    capture = robot.createScreenCapture(new Rectangle(0, 0, 1920, 1080));
                    if (i == 0) {
                        lastScreanCapture = capture;
                        i++;
                    }
                    diff = getDifferenceImage(lastScreanCapture, capture);
                    if (diff >= 4) {
                        try {
                            System.out.println("New screenshot was taken!");
                            File outputfile = new File("photos/photo" + i +".jpg");
                            ImageIO.write((RenderedImage) capture, "jpg", outputfile);
                            i++;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    lastScreanCapture = capture;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    // Thread.currentThread();
                } catch (Exception e) {}
                // System.out.println("new["+ i +"], difference = " + diff + "%");
            }
        };
        Timer timer = new Timer("MyTimer");
        timer.scheduleAtFixedRate(task, 30, 1000);
    }
}