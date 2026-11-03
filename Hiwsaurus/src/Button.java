import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Button {
    private final Rectangle bounds;
    private final String label;
    private final boolean styled;
    private boolean hovered;
    private boolean selected;
    private Color accent = new Color(132, 226, 92);

    private Button(int x, int y, int w, int h, String label, boolean styled) {
        this.bounds = new Rectangle(x, y, w, h);
        this.label = label;
        this.styled = styled;
    }

    public static Button styled(int x, int y, int w, int h, String label) {
        return new Button(x, y, w, h, label, true);
    }

    public static Button overlay(int x, int y, int w, int h) {
        return new Button(x, y, w, h, "", false);
    }

    public boolean contains(int px, int py) {
        return bounds.contains(px, py);
    }

    public void updateHover(int px, int py) {
        hovered = bounds.contains(px, py);
    }

    public void setHovered(boolean value) {
        hovered = value;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setSelected(boolean value) {
        selected = value;
    }

    public void setAccent(Color color) {
        accent = color;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void draw(Graphics2D g) {
        if (styled) {
            drawStyled(g);
        } else {
            drawOverlay(g);
        }
    }

    private void drawStyled(Graphics2D g) {
        int arc = Math.min(bounds.height / 2, 44);
        Color fill = selected ? new Color(255, 214, 66) : hovered ? brighten(accent) : accent;
        g.setColor(new Color(10, 10, 40, 150));
        g.fillRoundRect(bounds.x, bounds.y + 9, bounds.width, bounds.height, arc, arc);
        g.setColor(new Color(28, 28, 78));
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, arc, arc);
        g.setColor(fill);
        g.fillRoundRect(bounds.x + 8, bounds.y + 8, bounds.width - 16, bounds.height - 16, arc - 8, arc - 8);
        g.setColor(new Color(255, 255, 255, 70));
        g.fillRoundRect(bounds.x + 16, bounds.y + 14, bounds.width - 32, bounds.height / 4, arc / 2, arc / 2);
        Font font = UI.fitFont(g, label, Font.BOLD, (int) (bounds.height * 0.46), bounds.width - 40);
        int baseline = bounds.y + bounds.height / 2 + (int) (font.getSize() * 0.35);
        UI.drawCentered(g, label, bounds.getCenterX(), baseline, font, new Color(30, 40, 100));
    }

    private void drawOverlay(Graphics2D g) {
        if (!hovered) {
            return;
        }
        Composite old = g.getComposite();
        g.setStroke(new BasicStroke(10f));
        g.setColor(new Color(255, 240, 120, 90));
        g.drawRoundRect(bounds.x - 8, bounds.y - 8, bounds.width + 16, bounds.height + 16, 50, 50);
        g.setStroke(new BasicStroke(5f));
        g.setColor(new Color(255, 255, 255, 235));
        g.drawRoundRect(bounds.x - 4, bounds.y - 4, bounds.width + 8, bounds.height + 8, 44, 44);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
        g.setColor(Color.WHITE);
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 44, 44);
        g.setComposite(old);
    }

    private static Color brighten(Color color) {
        return new Color(Math.min(255, color.getRed() + 55), Math.min(255, color.getGreen() + 25),
                Math.min(255, color.getBlue() + 40));
    }
}
