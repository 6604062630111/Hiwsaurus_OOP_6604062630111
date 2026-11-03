import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ResultScene extends Scene {
    private static final int BUTTON_W = 420;
    private static final int BUTTON_H = 110;
    private static final int LEFT_X = 170;
    private static final int RIGHT_X = Const.WIDTH - 170 - BUTTON_W;

    private final Navigator navigator;
    private final LevelConfig config;
    private final BufferedImage image;
    private final List<Button> buttons = new ArrayList<>();
    private final List<Runnable> actions = new ArrayList<>();
    private double age;

    public ResultScene(Navigator navigator, LevelConfig config, boolean victory) {
        this.navigator = navigator;
        this.config = config;
        String name = (victory ? "VICTORY" : "GAMEOVER") + config.getChapter();
        this.image = Assets.image("screens/" + name + ".png");
        LevelConfig nextLevel = victory ? LevelConfig.next(config) : null;
        if (nextLevel != null) {
            Button next = Button.styled(RIGHT_X, 820, BUTTON_W, BUTTON_H + 10, "NEXT LEVEL");
            next.setAccent(new Color(255, 214, 66));
            add(next, () -> navigator.startLevel(nextLevel));
            add(Button.styled(LEFT_X, 760, BUTTON_W, BUTTON_H, "RETRY"), () -> navigator.startLevel(config));
            add(Button.styled(LEFT_X, 900, BUTTON_W, BUTTON_H, "MENU"), navigator::showMenu);
        } else {
            add(Button.styled(LEFT_X, 830, BUTTON_W, BUTTON_H, "RETRY"), () -> navigator.startLevel(config));
            add(Button.styled(RIGHT_X, 830, BUTTON_W, BUTTON_H, "MENU"), navigator::showMenu);
        }
    }

    private void add(Button button, Runnable action) {
        buttons.add(button);
        actions.add(action);
    }

    @Override
    public void update(double dt) {
        age += dt;
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage(image, 0, 0, Const.WIDTH, Const.HEIGHT, null);
        for (Button button : buttons) {
            button.draw(g);
        }
        if (age < 0.4) {
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1 - age / 0.4)));
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Const.WIDTH, Const.HEIGHT);
            g.setComposite(old);
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
            actions.get(0).run();
        } else if (keyCode == KeyEvent.VK_R) {
            navigator.startLevel(config);
        } else if (keyCode == KeyEvent.VK_M || keyCode == KeyEvent.VK_ESCAPE) {
            navigator.showMenu();
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        for (Button button : buttons) {
            button.updateHover(x, y);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).contains(x, y)) {
                actions.get(i).run();
                return;
            }
        }
    }
}
