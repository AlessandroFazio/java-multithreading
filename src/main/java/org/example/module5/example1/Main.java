package org.example.module5.example1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String SOURCE_FILE = "./resources/many-flower.jpg";
    public static final String DEST_FILE=".out/many-flowers.jpg";
    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage =
                new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        recolorSingleThreaded(originalImage, resultImage);

        File outputFile = new File(DEST_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);

    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int blue = getBlue(rgb);
        int green = getGreen(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGray(red, green, blue)) {
            newRed = Math.min(red + 10, 255);
            newGreen = Math.max(green - 80, 0);
            newBlue = Math.max(blue - 20, 0);
        } else {
            newRed = red;
            newBlue = blue;
            newGreen = green;
        }

        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
    }
    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    public static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads;

        for(int i=0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;
             Thread thread = new Thread(() -> {
                 int leftCorner = 0;
                 int topCorner = height * threadMultiplier;

                 recolorImage(originalImage, resultImage, leftCorner, topCorner, width, height);
             });
            threads.add(thread);
        }
        for(Thread thread: threads) {
            thread.start();
        }

        for(Thread thread: threads) {
            try {
                thread.join();
            } catch(InterruptedException e) {
            }
        }
    }
    public static void recolorImage(
            BufferedImage originalImage, BufferedImage resultImage, int leftCorner,
            int topCorner, int width, int height) {
        for(int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for(int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }
}
