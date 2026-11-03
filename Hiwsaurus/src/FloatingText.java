import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;

public class FloatingText {
    private final String text;
    private final Color color;
    private final int size;
    private final double maxLife;
    private final double rise;
    private double x;
    private double y;
    private double life;

    public FloatingText(String text, double x, double y, Color color, int size, double life, double rise) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
        this.life = life;
        this.maxLife = life;
        this.rise = rise;
    }

    public void update(double dt) {
        life -= dt;
        y -= rise * dt;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public void draw(Graphics2D g) {
        float alpha = (float) Math.max(0, Math.min(1, life / maxLife * 1.6));
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        UI.drawOutlined(g, text, x, y, UI.font(java.awt.Font.BOLD, size), color, Color.BLACK, size / 7f);
        g.setComposite(old);
    }
}
