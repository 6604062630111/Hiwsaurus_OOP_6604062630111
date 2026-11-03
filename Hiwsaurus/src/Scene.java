import java.awt.Graphics2D;

public abstract class Scene {
    public abstract void update(double dt);

    public abstract void render(Graphics2D g);

    public void keyPressed(int keyCode) {
    }

    public void keyReleased(int keyCode) {
    }

    public void mouseMoved(int x, int y) {
    }

    public void mousePressed(int x, int y) {
    }

    public void focusLost() {
    }

    public String getMusic() {
        return null;
    }
}
