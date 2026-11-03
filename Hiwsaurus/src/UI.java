import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;

public final class UI {
    private UI() {
    }

    public static Font font(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    public static int textWidth(Graphics2D g, String text, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    public static void drawText(Graphics2D g, String text, double x, double baseline, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, (float) x, (float) baseline);
    }

    public static void drawCentered(Graphics2D g, String text, double centerX, double baseline, Font font, Color color) {
        int w = textWidth(g, text, font);
        drawText(g, text, centerX - w / 2.0, baseline, font, color);
    }

    public static void drawOutlined(Graphics2D g, String text, double x, double baseline, Font font,
                                    Color fill, Color outline, float outlineWidth) {
        GlyphVector vector = font.createGlyphVector(g.getFontRenderContext(), text);
        Shape shape = vector.getOutline((float) x, (float) baseline);
        g.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(outline);
        g.draw(shape);
        g.setColor(fill);
        g.fill(shape);
    }

    public static void drawCenteredOutlined(Graphics2D g, String text, double centerX, double baseline, Font font,
                                            Color fill, Color outline, float outlineWidth) {
        int w = textWidth(g, text, font);
        drawOutlined(g, text, centerX - w / 2.0, baseline, font, fill, outline, outlineWidth);
    }

    public static Font fitFont(Graphics2D g, String text, int style, int maxSize, double maxWidth) {
        int size = maxSize;
        Font font = font(style, size);
        while (size > 10 && textWidth(g, text, font) > maxWidth) {
            size -= 2;
            font = font(style, size);
        }
        return font;
    }

    public static void drawFittedCentered(Graphics2D g, String text, double centerX, double baseline,
                                          int maxSize, double maxWidth, Color color) {
        Font font = fitFont(g, text, Font.BOLD, maxSize, maxWidth);
        drawCentered(g, text, centerX, baseline, font, color);
    }
}
