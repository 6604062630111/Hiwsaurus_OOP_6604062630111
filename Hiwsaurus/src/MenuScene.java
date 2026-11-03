import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class MenuScene extends Scene {
    private static final int VISIBLE_WIDTH = 1878;

    private final Navigator navigator;
    private final BufferedImage background;
    private final Button startButton;
    private final Button exitButton;

    public MenuScene(Navigator navigator) {
        this.navigator = navigator;
        this.background = Assets.image("screens/menu_bg.jpg");
        this.startButton = Button.overlay(stretch(612), 574, stretch(617), 124);
        this.exitButton = Button.overlay(stretch(692), 807, stretch(456), 155);
    }

    private static int stretch(int value) {
        return (int) Math.round(value * Const.WIDTH / (double) VISIBLE_WIDTH);
    }

    @Override
    public String getMusic() {
        return Music.MENU;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(background, 0, 0, Const.WIDTH, Const.HEIGHT, 0, 0, VISIBLE_WIDTH, Const.HEIGHT, null);
        startButton.draw(g);
        exitButton.draw(g);
        Font font = UI.font(Font.BOLD, 34);
        UI.drawCenteredOutlined(g, "Press ENTER to start", Const.WIDTH / 2.0, 1050, font,
                Color.WHITE, new Color(20, 20, 40), 6f);
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
            navigator.showLevelSelect(1);
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            navigator.exitGame();
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        startButton.updateHover(x, y);
        exitButton.updateHover(x, y);
    }

    @Override
    public void mousePressed(int x, int y) {
        if (startButton.contains(x, y)) {
            navigator.showLevelSelect(1);
        } else if (exitButton.contains(x, y)) {
            navigator.exitGame();
        }
    }
}
