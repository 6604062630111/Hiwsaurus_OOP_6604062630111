import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Navigator {
    private final Timer timer;
    private Scene scene;
    private long lastTime;
    private double scale = 1;
    private double offsetX;
    private double offsetY;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        setDoubleBuffered(true);
        timer = new Timer(15, e -> tick());
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                scene.keyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                scene.keyReleased(e.getKeyCode());
            }
        });
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                scene.mouseMoved(toLogicalX(e), toLogicalY(e));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                scene.mousePressed(toLogicalX(e), toLogicalY(e));
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                scene.focusLost();
            }
        });
        scene = new MenuScene(this);
    }

    public void start() {
        lastTime = System.nanoTime();
        Music.play(scene.getMusic());
        timer.start();
        requestFocusInWindow();
    }

    private int toLogicalX(MouseEvent e) {
        return (int) ((e.getX() - offsetX) / scale);
    }

    private int toLogicalY(MouseEvent e) {
        return (int) ((e.getY() - offsetY) / scale);
    }

    private void tick() {
        long now = System.nanoTime();
        double dt = Math.min(0.05, (now - lastTime) / 1_000_000_000.0);
        lastTime = now;
        scene.update(dt);
        repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        scale = Math.min(getWidth() / (double) Const.WIDTH, getHeight() / (double) Const.HEIGHT);
        offsetX = (getWidth() - Const.WIDTH * scale) / 2.0;
        offsetY = (getHeight() - Const.HEIGHT * scale) / 2.0;
        g.translate(offsetX, offsetY);
        g.scale(scale, scale);
        g.setClip(0, 0, Const.WIDTH, Const.HEIGHT);
        scene.render(g);
        g.dispose();
    }

    private void switchTo(Scene next) {
        scene = next;
        Music.play(next.getMusic());
        lastTime = System.nanoTime();
    }

    @Override
    public void showMenu() {
        switchTo(new MenuScene(this));
    }

    @Override
    public void showLevelSelect(int chapter) {
        switchTo(new LevelSelectScene(this, chapter));
    }

    @Override
    public void startLevel(LevelConfig config) {
        switchTo(new PlayScene(this, config));
    }

    @Override
    public void showResult(LevelConfig config, boolean victory) {
        switchTo(new ResultScene(this, config, victory));
    }

    @Override
    public void exitGame() {
        System.exit(0);
    }
}
