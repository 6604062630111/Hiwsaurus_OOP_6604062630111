import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class LevelSelectScene extends Scene {
    private static final String[] CHAPTER_NAMES = {"Little Hiwsaurus", "Gym Rookie", "King of the Hunters"};
    private static final int[] BONE_Y = {50, 386, 778};
    private static final int BONE_X = 1204;
    private static final int BONE_W = 514;
    private static final int BONE_H = 259;

    private final Navigator navigator;
    private final BufferedImage background;
    private final Button[] chapterTabs = new Button[Const.TOTAL_CHAPTERS];
    private final Button[] bones = new Button[Const.LEVELS_PER_CHAPTER];
    private final Button backButton;
    private int chapter;

    public LevelSelectScene(Navigator navigator, int chapter) {
        this.navigator = navigator;
        this.chapter = chapter;
        this.background = Assets.image("screens/select_bg.jpg");
        for (int i = 0; i < chapterTabs.length; i++) {
            chapterTabs[i] = Button.styled(470 + i * 250, 36, 230, 80, "CHAPTER " + (i + 1));
        }
        for (int i = 0; i < bones.length; i++) {
            bones[i] = Button.overlay(BONE_X, BONE_Y[i], BONE_W, BONE_H);
        }
        backButton = Button.styled(640, 950, 300, 90, "BACK");
        refreshTabs();
    }

    private void refreshTabs() {
        for (int i = 0; i < chapterTabs.length; i++) {
            chapterTabs[i].setSelected(i + 1 == chapter);
        }
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
        g.drawImage(background, 0, 0, Const.WIDTH, Const.HEIGHT, null);
        for (Button tab : chapterTabs) {
            tab.draw(g);
        }
        Font nameFont = UI.font(Font.BOLD, 40);
        String name = CHAPTER_NAMES[chapter - 1];
        int plateWidth = UI.textWidth(g, name, nameFont) + 70;
        g.setColor(new Color(20, 20, 50, 185));
        g.fillRoundRect(720 - plateWidth / 2 + 150, 140, plateWidth, 66, 34, 34);
        UI.drawCentered(g, name, 870, 188, nameFont, new Color(255, 236, 140));
        for (Button bone : bones) {
            bone.draw(g);
        }
        drawCaptions(g);
        backButton.draw(g);
    }

    private void drawCaptions(Graphics2D g) {
        Font font = UI.font(Font.BOLD, 36);
        for (int i = 0; i < Const.LEVELS_PER_CHAPTER; i++) {
            LevelConfig config = LevelConfig.get(chapter, i + 1);
            String text = config.isBoss() ? "BOSS FIGHT" : "Goal: " + config.getTargetPoints() + " pts";
            Color fill = config.isBoss() ? new Color(255, 90, 80) : Color.WHITE;
            int cx = BONE_X + BONE_W / 2;
            int y = i < 2 ? BONE_Y[i] + BONE_H + 52 : BONE_Y[i] - 14;
            int w = UI.textWidth(g, text, font) + 50;
            g.setColor(new Color(20, 20, 50, 190));
            g.fillRoundRect(cx - w / 2, y - 42, w, 58, 30, 30);
            UI.drawCentered(g, text, cx, y, font, fill);
        }
    }

    private void startLevel(int level) {
        navigator.startLevel(LevelConfig.get(chapter, level));
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            chapter = Math.max(1, chapter - 1);
            refreshTabs();
        } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            chapter = Math.min(Const.TOTAL_CHAPTERS, chapter + 1);
            refreshTabs();
        } else if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_3) {
            startLevel(keyCode - KeyEvent.VK_0);
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            navigator.showMenu();
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        for (Button tab : chapterTabs) {
            tab.updateHover(x, y);
        }
        for (Button bone : bones) {
            bone.updateHover(x, y);
        }
        backButton.updateHover(x, y);
    }

    @Override
    public void mousePressed(int x, int y) {
        for (int i = 0; i < chapterTabs.length; i++) {
            if (chapterTabs[i].contains(x, y)) {
                chapter = i + 1;
                refreshTabs();
                return;
            }
        }
        for (int i = 0; i < bones.length; i++) {
            if (bones[i].contains(x, y)) {
                startLevel(i + 1);
                return;
            }
        }
        if (backButton.contains(x, y)) {
            navigator.showMenu();
        }
    }
}
