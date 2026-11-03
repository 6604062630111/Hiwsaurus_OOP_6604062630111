import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite {
    private final BufferedImage image;
    private final double scale;
    private final int footRow;

    public Sprite(BufferedImage image, double scale) {
        this.image = image;
        this.scale = scale;
        this.footRow = findFootRow(image);
    }

    public static Sprite fitting(BufferedImage image, double maxSide) {
        return new Sprite(image, maxSide / Math.max(image.getWidth(), image.getHeight()));
    }

    private static int findFootRow(BufferedImage img) {
        for (int y = img.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < img.getWidth(); x++) {
                if ((img.getRGB(x, y) >>> 24) > 40) {
                    return y;
                }
            }
        }
        return img.getHeight() - 1;
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getScale() {
        return scale;
    }

    public int getWidth() {
        return (int) Math.round(image.getWidth() * scale);
    }

    public int getHeight() {
        return (int) Math.round(image.getHeight() * scale);
    }

    public int getFootOffset() {
        return (int) Math.round((footRow + 1) * scale);
    }

    public void draw(Graphics2D g, double x, double y) {
        g.drawImage(image, (int) Math.round(x), (int) Math.round(y), getWidth(), getHeight(), null);
    }

    public void drawFeet(Graphics2D g, double centerX, double footY, boolean flip) {
        double x = centerX - getWidth() / 2.0;
        double y = footY - getFootOffset();
        if (!flip) {
            draw(g, x, y);
            return;
        }
        AffineTransform old = g.getTransform();
        g.translate(centerX, 0);
        g.scale(-1, 1);
        g.translate(-centerX, 0);
        draw(g, x, y);
        g.setTransform(old);
    }

    public void drawRotated(Graphics2D g, double centerX, double centerY, double angle) {
        AffineTransform old = g.getTransform();
        g.rotate(angle, centerX, centerY);
        draw(g, centerX - getWidth() / 2.0, centerY - getHeight() / 2.0);
        g.setTransform(old);
    }

    public Sprite silhouette() {
        BufferedImage white = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int alpha = image.getRGB(x, y) >>> 24;
                white.setRGB(x, y, (alpha << 24) | 0xFFFFFF);
            }
        }
        return new Sprite(white, scale);
    }
}
