import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.Random;

public class Boss extends GameObject {
    private static final double[] DISPLAY_SCALE = {1.7, 1.45, 1.55};

    private final Sprite sprite;
    private final Sprite flash;
    private final int maxHp;
    private final double speed;
    private final double fieldWidth;
    private final Random random = new Random();
    private int hp;
    private double lagHp;
    private int direction = 1;
    private double turnTimer;
    private double hitFlash;
    private boolean defeated;
    private double fade;
    private double walkPhase;

    public Boss(int chapter, int maxHp, double speed, double fieldWidth) {
        super(0, 70, 0, 0);
        this.sprite = new Sprite(Assets.image("boss/boss" + chapter + ".png"), DISPLAY_SCALE[chapter - 1]);
        this.flash = sprite.silhouette();
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.lagHp = maxHp;
        this.speed = speed;
        this.fieldWidth = fieldWidth;
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        this.x = (fieldWidth - width) / 2.0;
        this.turnTimer = 1.2 + random.nextDouble() * 1.5;
    }

    @Override
    public void update(double dt) {
        if (hitFlash > 0) {
            hitFlash -= dt;
        }
        if (lagHp > hp) {
            lagHp = Math.max(hp, lagHp - maxHp * 0.3 * dt);
        }
        if (defeated) {
            fade = Math.min(1.0, fade + dt / 1.4);
            return;
        }
        walkPhase += dt * (9 + speed / 40);
        x += direction * speed * dt;
        if (x < 0) {
            x = 0;
            direction = 1;
        } else if (x > fieldWidth - width) {
            x = fieldWidth - width;
            direction = -1;
        }
        turnTimer -= dt;
        if (turnTimer <= 0) {
            direction = -direction;
            turnTimer = 1.2 + random.nextDouble() * 1.8;
        }
    }

    public void damage(int amount) {
        hp = Math.max(0, hp - amount);
        hitFlash = 0.16;
    }

    public void startDefeat() {
        defeated = true;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public double getLagRatio() {
        return lagHp / maxHp;
    }

    public double getHpRatio() {
        return (double) hp / maxHp;
    }

    public double getMouthY() {
        return y + height * 0.75;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void draw(Graphics2D g) {
        Composite old = g.getComposite();
        java.awt.geom.AffineTransform oldTransform = g.getTransform();
        double centerX = x + width / 2.0;
        double footY = y + sprite.getFootOffset();
        double step = Math.abs(Math.sin(walkPhase));
        double lift = defeated ? 0 : step * height * 0.05;
        double tilt = defeated ? Math.sin(walkPhase * 4) * 0.05 * (1 - fade) : direction * 0.04 + Math.sin(walkPhase) * 0.045;
        double scaleX = 1 + (1 - step) * 0.03;
        double scaleY = 1 - (1 - step) * 0.04;
        double shakeX = hitFlash > 0 ? (Math.random() - 0.5) * 10 : 0;
        double dropY = 0;
        if (defeated) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1.0 - fade)));
            dropY = fade * 60;
            scaleX = 1;
            scaleY = 1;
        }
        g.translate(centerX + shakeX, footY - lift + dropY);
        g.rotate(tilt);
        g.scale(scaleX, scaleY);
        g.translate(-centerX, -footY);
        boolean flip = direction < 0;
        sprite.drawFeet(g, centerX, footY, flip);
        if (hitFlash > 0 && !defeated) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
            flash.drawFeet(g, centerX, footY, flip);
        }
        g.setTransform(oldTransform);
        g.setComposite(old);
    }
}
