import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public abstract class Collectible extends GameObject {
    private final Sprite sprite;
    private final double speed;
    private final double phase;
    private double age;

    protected Collectible(Sprite sprite, double centerX, double startY, double speed) {
        super(centerX - sprite.getWidth() / 2.0, startY, sprite.getWidth(), sprite.getHeight());
        this.sprite = sprite;
        this.speed = speed;
        this.phase = Math.random() * Math.PI * 2;
    }

    public abstract int getPoints();

    public abstract int getBossDamage();

    public abstract boolean isHarmful();

    @Override
    public void update(double dt) {
        age += dt;
        y += speed * dt;
    }

    protected double hitboxScale() {
        return 0.8;
    }

    @Override
    public Rectangle2D getBounds() {
        double w = width * hitboxScale();
        double h = height * hitboxScale();
        return new Rectangle2D.Double(x + (width - w) / 2.0, y + (height - h) / 2.0, w, h);
    }

    @Override
    public void draw(Graphics2D g) {
        double angle = Math.sin(age * 3 + phase) * 0.16;
        sprite.drawRotated(g, x + width / 2.0, y + height / 2.0, angle);
    }

    public boolean isBelow(double limit) {
        return y > limit;
    }
}
