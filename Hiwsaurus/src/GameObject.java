import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    protected GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update(double dt);

    public abstract void draw(Graphics2D g);

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getCenterX() {
        return x + width / 2.0;
    }
}
