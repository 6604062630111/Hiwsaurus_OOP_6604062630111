import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PlayScene extends Scene {
    private enum Phase {
        INTRO, PLAYING, PAUSED, WIN, LOSE
    }

    private final Navigator navigator;
    private final LevelConfig config;
    private final Player player;
    private final Boss boss;
    private final List<Collectible> items = new ArrayList<>();
    private final List<FloatingText> fieldTexts = new ArrayList<>();
    private final List<FloatingText> hudTexts = new ArrayList<>();
    private final Random random = new Random();
    private final BufferedImage background;
    private final BufferedImage leftPanel;
    private final BufferedImage heartImage;
    private final Button resumeButton;
    private final Button retryButton;
    private final Button menuButton;
    private Phase phase = Phase.INTRO;
    private double phaseTimer = 2.6;
    private double spawnTimer = 0.6;
    private double shake;
    private double hurtFlash;
    private double lastSpawnX = -1000;
    private boolean lastWasVegetable;
    private int points;
    private int lives = Const.MAX_LIVES;

    public PlayScene(Navigator navigator, LevelConfig config) {
        this.navigator = navigator;
        this.config = config;
        this.player = new Player(config.getChapter(), config.getPlayerSpeed(), Const.FIELD_W, Const.FIELD_H);
        this.boss = config.isBoss()
                ? new Boss(config.getChapter(), config.getBossHp(), config.getBossSpeed(), Const.FIELD_W) : null;
        this.background = Assets.image("bg/bg" + config.getChapter() + ".jpg");
        this.leftPanel = Assets.image("ui/left_panel.jpg");
        this.heartImage = Assets.image("ui/heart.png");
        int bx = (Const.WIDTH - 480) / 2;
        resumeButton = Button.styled(bx, 400, 480, 100, "RESUME");
        retryButton = Button.styled(bx, 530, 480, 100, "RETRY");
        menuButton = Button.styled(bx, 660, 480, 100, "MENU");
    }

    @Override
    public String getMusic() {
        return config.isBoss() ? Music.BOSS : Music.PLAY;
    }

    @Override
    public void update(double dt) {
        if (phase == Phase.PAUSED) {
            return;
        }
        updateTexts(dt);
        shake = Math.max(0, shake - dt);
        hurtFlash = Math.max(0, hurtFlash - dt);
        switch (phase) {
            case INTRO:
                player.update(dt);
                phaseTimer -= dt;
                if (phaseTimer <= 0) {
                    phase = Phase.PLAYING;
                }
                break;
            case PLAYING:
                updatePlaying(dt);
                break;
            case WIN:
            case LOSE:
                player.update(dt);
                if (boss != null) {
                    boss.update(dt);
                }
                phaseTimer -= dt;
                if (phaseTimer <= 0) {
                    navigator.showResult(config, phase == Phase.WIN);
                }
                break;
            default:
                break;
        }
    }

    private void updateTexts(double dt) {
        for (Iterator<FloatingText> it = fieldTexts.iterator(); it.hasNext(); ) {
            FloatingText text = it.next();
            text.update(dt);
            if (text.isDead()) {
                it.remove();
            }
        }
        for (Iterator<FloatingText> it = hudTexts.iterator(); it.hasNext(); ) {
            FloatingText text = it.next();
            text.update(dt);
            if (text.isDead()) {
                it.remove();
            }
        }
    }

    private void updatePlaying(double dt) {
        player.update(dt);
        if (boss != null) {
            boss.update(dt);
        }
        spawnTimer -= dt;
        if (spawnTimer <= 0) {
            spawnItem();
            double interval = config.getSpawnInterval();
            if (boss != null && boss.getHpRatio() < 0.5) {
                interval *= 0.88;
            }
            spawnTimer = interval * (0.8 + random.nextDouble() * 0.4);
        }
        for (Collectible item : items) {
            item.update(dt);
        }
        handleCollisions();
        items.removeIf(item -> item.isBelow(Const.FIELD_H + 20));
        checkEnding();
    }

    private void spawnItem() {
        double margin = 70;
        double x;
        if (boss != null) {
            x = boss.getCenterX() + (random.nextDouble() - 0.5) * boss.getWidth() * 0.7;
        } else {
            x = margin + random.nextDouble() * (Const.FIELD_W - margin * 2);
        }
        double minGap = player.getHitbox().getWidth() + 150;
        for (int attempt = 0; attempt < 8 && Math.abs(x - lastSpawnX) < minGap; attempt++) {
            x = margin + random.nextDouble() * (Const.FIELD_W - margin * 2);
            if (boss != null) {
                x = boss.getCenterX() + (random.nextDouble() - 0.5) * boss.getWidth() * 0.9;
            }
        }
        x = Math.max(margin, Math.min(Const.FIELD_W - margin, x));
        lastSpawnX = x;
        double startY = boss != null ? boss.getMouthY() : -90;
        double speed = config.getMinSpeed() + random.nextDouble() * (config.getMaxSpeed() - config.getMinSpeed());
        double vegetableChance = lastWasVegetable ? config.getVegetableChance() * 0.35 : config.getVegetableChance();
        Collectible item;
        if (random.nextDouble() < vegetableChance) {
            item = ItemFactory.createVegetable(x, startY, speed);
            lastWasVegetable = true;
        } else {
            double roll = random.nextDouble();
            if (roll < config.getWheyChance()) {
                item = ItemFactory.createWhey(x, startY, speed);
            } else if (roll < config.getWheyChance() + config.getEquipmentChance()) {
                item = ItemFactory.createEquipment(x, startY, speed);
            } else {
                item = ItemFactory.createMeat(x, startY, speed);
            }
            lastWasVegetable = false;
        }
        items.add(item);
    }

    private void handleCollisions() {
        Rectangle2D hitbox = player.getHitbox();
        Iterator<Collectible> it = items.iterator();
        while (it.hasNext()) {
            Collectible item = it.next();
            if (!item.getBounds().intersects(hitbox)) {
                continue;
            }
            if (item.isHarmful()) {
                if (player.isInvulnerable()) {
                    continue;
                }
                Sounds.play(Sounds.BAD);
                loseLife();
                it.remove();
            } else {
                collect(item);
                it.remove();
            }
        }
    }

    private void collect(Collectible item) {
        Sounds.play(Sounds.EAT);
        points += item.getPoints();
        player.celebrate();
        if (boss != null) {
            boss.damage(item.getBossDamage());
        }
        hudTexts.add(new FloatingText("+" + item.getPoints(), 1815, 392, new Color(90, 255, 120), 38, 0.9, 40));
        fieldTexts.add(new FloatingText("+" + item.getPoints(), item.getCenterX(), item.getY() - 6,
                new Color(255, 236, 90), 46, 0.8, 90));
    }

    private void loseLife() {
        lives--;
        player.hurt();
        shake = 0.3;
        hurtFlash = 0.45;
        hudTexts.add(new FloatingText("-1", 1830, 545, new Color(255, 70, 70), 44, 1.0, 40));
    }

    private void checkEnding() {
        if (lives <= 0) {
            endLevel(false);
        } else if (boss != null && boss.getHp() <= 0) {
            boss.startDefeat();
            Sounds.play(Sounds.BOSS_DIE);
            endLevel(true);
        } else if (boss == null && points >= config.getTargetPoints()) {
            endLevel(true);
        }
    }

    private void endLevel(boolean win) {
        phase = win ? Phase.WIN : Phase.LOSE;
        phaseTimer = win ? 1.9 : 1.6;
        items.clear();
        player.stopMoving();
        player.setFinalMood(win ? Player.Mood.LOVE : Player.Mood.SAD);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Const.WIDTH, Const.HEIGHT);
        g.drawImage(leftPanel, 0, 0, null);
        renderField(g);
        renderHud(g);
        if (phase == Phase.PAUSED) {
            renderPause(g);
        }
    }

    private void renderField(Graphics2D g) {
        Graphics2D fg = (Graphics2D) g.create();
        double sx = shake > 0 ? (random.nextDouble() - 0.5) * 14 : 0;
        double sy = shake > 0 ? (random.nextDouble() - 0.5) * 14 : 0;
        fg.setClip(Const.FIELD_X, Const.FIELD_Y, Const.FIELD_W, Const.FIELD_H);
        fg.translate(Const.FIELD_X + sx, Const.FIELD_Y + sy);
        fg.drawImage(background, 0, 0, null);
        if (boss != null) {
            boss.draw(fg);
        }
        for (Collectible item : items) {
            item.draw(fg);
        }
        player.draw(fg);
        for (FloatingText text : fieldTexts) {
            text.draw(fg);
        }
        if (boss != null) {
            renderBossBar(fg);
        }
        if (hurtFlash > 0) {
            Composite old = fg.getComposite();
            fg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.min(0.35, hurtFlash)));
            fg.setColor(new Color(255, 0, 0));
            fg.fillRect(0, 0, Const.FIELD_W, Const.FIELD_H);
            fg.setComposite(old);
        }
        if (phase == Phase.INTRO) {
            renderIntro(fg);
        }
        fg.dispose();
    }

    private void renderBossBar(Graphics2D g) {
        int barW = 760;
        int barH = 34;
        int bx = (Const.FIELD_W - barW) / 2 + 40;
        int by = 20;
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRoundRect(bx - 110, by - 12, barW + 140, barH + 24, 36, 36);
        Sprite icon = new Sprite(boss.getSprite().getImage(), 0.5);
        icon.draw(g, bx - 100, by + barH / 2.0 - icon.getHeight() / 2.0);
        g.setColor(new Color(60, 10, 10));
        g.fillRoundRect(bx, by, barW, barH, 18, 18);
        int lagW = (int) (barW * boss.getLagRatio());
        g.setColor(new Color(255, 210, 60));
        g.fillRoundRect(bx, by, lagW, barH, 18, 18);
        int hpW = (int) (barW * boss.getHpRatio());
        if (hpW > 0) {
            g.setPaint(new GradientPaint(0, by, new Color(255, 120, 80), 0, by + barH, new Color(210, 20, 30)));
            g.fillRoundRect(bx, by, hpW, barH, 18, 18);
        }
        g.setColor(Color.WHITE);
        g.setStroke(new java.awt.BasicStroke(4f));
        g.drawRoundRect(bx, by, barW, barH, 18, 18);
        String text = "BOSS  " + boss.getHp() + " / " + boss.getMaxHp();
        UI.drawCenteredOutlined(g, text, bx + barW / 2.0, by + barH - 7, UI.font(Font.BOLD, 26),
                Color.WHITE, Color.BLACK, 5f);
    }

    private void renderIntro(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Const.FIELD_W, Const.FIELD_H);
        double cx = Const.FIELD_W / 2.0;
        UI.drawCenteredOutlined(g, "CHAPTER " + config.getChapter() + " - LEVEL " + config.getLevel(), cx, 360,
                UI.font(Font.BOLD, 76), Color.WHITE, new Color(20, 20, 20), 10f);
        String goal = config.isBoss() ? "BOSS FIGHT! Catch meat to hurt the boss"
                : "Collect " + config.getTargetPoints() + " points to clear the level";
        UI.drawCenteredOutlined(g, goal, cx, 450, UI.font(Font.BOLD, 40),
                config.isBoss() ? new Color(255, 120, 100) : new Color(255, 236, 120), new Color(20, 20, 20), 8f);
        UI.drawCenteredOutlined(g, "Catch meat, whey and gym gear. Avoid vegetables!", cx, 520,
                UI.font(Font.BOLD, 34), Color.WHITE, new Color(20, 20, 20), 7f);
        UI.drawCenteredOutlined(g, "Move: Arrow keys or A / D     Pause: ESC", cx, 580,
                UI.font(Font.BOLD, 30), new Color(190, 230, 255), new Color(20, 20, 20), 6f);
    }

    private void renderHud(Graphics2D g) {
        double cx = Const.HUD_X + Const.HUD_W / 2.0;
        UI.drawFittedCentered(g, "Level " + config.getLevel(), cx, 108, 112, 330, Color.WHITE);
        UI.drawCentered(g, "Chapter " + config.getChapter(), cx, 178, UI.font(Font.BOLD, 46), new Color(170, 255, 140));
        if (config.isBoss()) {
            UI.drawCentered(g, "BOSS FIGHT", cx, 238, UI.font(Font.BOLD, 50), new Color(255, 80, 70));
        }
        UI.drawFittedCentered(g, "Points : " + points, cx, 448, 70, 330, Color.WHITE);
        g.drawImage(heartImage, 1590, 505, null);
        UI.drawText(g, ": " + lives, 1790, 610, UI.font(Font.BOLD, 96), Color.WHITE);
        if (config.isBoss()) {
            UI.drawFittedCentered(g, "Catch meat", cx, 790, 40, 320, new Color(255, 236, 120));
            UI.drawFittedCentered(g, "to hurt the boss!", cx, 840, 40, 320, new Color(255, 236, 120));
        } else {
            UI.drawCentered(g, "Goal : " + config.getTargetPoints(), cx, 790, UI.font(Font.BOLD, 52),
                    new Color(255, 236, 120));
            int barX = Const.HUD_X + 36;
            int barW = Const.HUD_W - 72;
            g.setColor(new Color(50, 50, 50));
            g.fillRoundRect(barX, 815, barW, 30, 16, 16);
            double ratio = Math.min(1.0, (double) points / config.getTargetPoints());
            g.setColor(new Color(110, 220, 90));
            g.fillRoundRect(barX, 815, (int) (barW * ratio), 30, 16, 16);
            g.setColor(Color.WHITE);
            g.setStroke(new java.awt.BasicStroke(3f));
            g.drawRoundRect(barX, 815, barW, 30, 16, 16);
        }
        UI.drawCentered(g, "A D / Arrows : move", cx, 1010, UI.font(Font.BOLD, 28), new Color(170, 170, 170));
        UI.drawCentered(g, "ESC : pause", cx, 1048, UI.font(Font.BOLD, 28), new Color(170, 170, 170));
        for (FloatingText text : hudTexts) {
            text.draw(g);
        }
    }

    private void renderPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, Const.WIDTH, Const.HEIGHT);
        UI.drawCenteredOutlined(g, "PAUSED", Const.WIDTH / 2.0, 330, UI.font(Font.BOLD, 110),
                Color.WHITE, new Color(20, 20, 20), 12f);
        resumeButton.draw(g);
        retryButton.draw(g);
        menuButton.draw(g);
    }

    private void togglePause() {
        if (phase == Phase.PLAYING) {
            phase = Phase.PAUSED;
            player.stopMoving();
        } else if (phase == Phase.PAUSED) {
            phase = Phase.PLAYING;
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P) {
            togglePause();
            return;
        }
        if (phase == Phase.PAUSED) {
            if (keyCode == KeyEvent.VK_ENTER) {
                togglePause();
            } else if (keyCode == KeyEvent.VK_R) {
                navigator.startLevel(config);
            } else if (keyCode == KeyEvent.VK_M) {
                navigator.showMenu();
            }
            return;
        }
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            player.setMovingLeft(true);
        } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            player.setMovingRight(true);
        } else if (phase == Phase.INTRO && (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE)) {
            phaseTimer = 0;
        }
    }

    @Override
    public void keyReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            player.setMovingLeft(false);
        } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            player.setMovingRight(false);
        }
    }

    @Override
    public void focusLost() {
        player.stopMoving();
        if (phase == Phase.PLAYING) {
            phase = Phase.PAUSED;
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        if (phase == Phase.PAUSED) {
            resumeButton.updateHover(x, y);
            retryButton.updateHover(x, y);
            menuButton.updateHover(x, y);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if (phase != Phase.PAUSED) {
            return;
        }
        if (resumeButton.contains(x, y)) {
            togglePause();
        } else if (retryButton.contains(x, y)) {
            navigator.startLevel(config);
        } else if (menuButton.contains(x, y)) {
            navigator.showMenu();
        }
    }
}
