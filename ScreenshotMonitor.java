import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ScreenshotMonitor {

    private static BufferedImage lastScreenshot = null;

    public static void main(String[] args) {
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int width = Integer.parseInt(args[2]);
            int height = Integer.parseInt(args[3]);

            Robot robot = new Robot();
            lastScreenshot = robot.createScreenCapture(new Rectangle(x, y, width, height));

            while (true) {
                BufferedImage currentScreenshot = robot.createScreenCapture(new Rectangle(x, y, width, height));

                if (hasContentChanged(lastScreenshot, currentScreenshot)) {
                    saveScreenshot(currentScreenshot);
                }

                lastScreenshot = currentScreenshot;

                // Fügen Sie hier eine geeignete Wartezeit ein, um die Belastung des Systems zu reduzieren.
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean hasContentChanged(BufferedImage lastScreenshot, BufferedImage currentScreenshot) {
        int width = currentScreenshot.getWidth();
        int height = currentScreenshot.getHeight();

        int totalPixels = width * height;
        int totalDifference = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int currentPixel = currentScreenshot.getRGB(x, y);
                int lastPixel = lastScreenshot.getRGB(x, y);

                int currentRed = (currentPixel >> 16) & 0xFF;
                int currentGreen = (currentPixel >> 8) & 0xFF;
                int currentBlue = currentPixel & 0xFF;

                int lastRed = (lastPixel >> 16) & 0xFF;
                int lastGreen = (lastPixel >> 8) & 0xFF;
                int lastBlue = lastPixel & 0xFF;

                int pixelDifference = Math.abs(currentRed - lastRed) +
                                      Math.abs(currentGreen - lastGreen) +
                                      Math.abs(currentBlue - lastBlue);

                totalDifference += pixelDifference;
            }
        }

        // Hier kannst du den Schwellenwert für die Änderung anpassen.
        double averageDifference = (double) totalDifference / totalPixels;
        double changeThreshold = 10.0; // Beispielwert, je nach Anforderungen anpassen.

        return averageDifference > changeThreshold;
    }

    private static void saveScreenshot(BufferedImage screenshot) {
        try {
            String filename = "screenshot" + System.currentTimeMillis() + ".png";
            // Verwende die aktuelle Systemzeit im Dateinamen, um Duplikate zu vermeiden.
            ImageIO.write(screenshot, "png", new File(filename));
            System.out.println("Screenshot saved: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}