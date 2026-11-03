import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Player extends GameObject {
    public enum Mood {
        NORMAL, LOVE, SAD
    }

    private static final double[] BODY_SCALE = {1.12, 1.75, 0.95};
    private static final double[] LOVE_SCALE = {1.12, 1.10, 0.95};
    private static final double[] SAD_SCALE = {1.12, 1.15, 0.95};
    private static final double[] HITBOX_WIDTH = {0.60, 0.55, 0.45};

    private final Sprite normal;
    private final Sprite love;
    private final Sprite sad;
    private final double speed;
    private final double fieldWidth;
    private final double hitboxWidth;
    private Mood mood = Mood.NORMAL;
    private double moodTimer;
    private double invulnerableTimer;
    private boolean movingLeft;
    private boolean movingRight;
    private boolean facingRight = true;
    private boolean walking;
    private double walkPhase;
    private double idleTime;

    public Player(int chapter, double speed, double fieldWidth, double fieldHeight) {
        super(0, 0, 0, 0);
        int index = chapter - 1;
        this.normal = new Sprite(Assets.image("player/MC" + chapter + ".png"), BODY_SCALE[index]);
        this.love = new Sprite(Assets.image("player/LOVE" + chapter + ".png"), LOVE_SCALE[index]);
        this.sad = new Sprite(Assets.image("player/SAD" + chapter + ".png"), SAD_SCALE[index]);
        this.speed = speed;
        this.fieldWidth = fieldWidth;
        this.hitboxWidth = HITBOX_WIDTH[index];
        this.width = normal.getWidth();
        this.height = normal.getHeight();
        this.x = (fieldWidth - width) / 2.0;
        this.y = fieldHeight - height - 6;
    }

    public void setMovingLeft(boolean value) {
        movingLeft = value;
    }

    public void setMovingRight(boolean value) {
        movingRight = value;
    }

    public void stopMoving() {
        movingLeft = false;
        movingRight = false;
    }

    @Override
    public void update(double dt) {
        double direction = (movingRight ? 1 : 0) - (movingLeft ? 1 : 0);
        if (direction > 0) {
            facingRight = true;
        } else if (direction < 0) {
            facingRight = false;
        }
        walking = direction != 0;
        idleTime += dt;
        if (walking) {
            walkPhase += dt * 15;
        }
        x += direction * speed * dt;
        x = Math.max(0, Math.min(fieldWidth - width, x));
        if (moodTimer > 0) {
            moodTimer -= dt;
            if (moodTimer <= 0) {
                mood = Mood.NORMAL;
            }
        }
        if (invulnerableTimer > 0) {
            invulnerableTimer -= dt;
        }
    }

    public Rectangle2D getHitbox() {
        double w = width * hitboxWidth;
        return new Rectangle2D.Double(x + (width - w) / 2.0, y + height * 0.12, w, height * 0.78);
    }

    public void celebrate() {
        if (mood != Mood.SAD) {
            mood = Mood.LOVE;
            moodTimer = 0.55;
        }
    }

    public void hurt() {
        mood = Mood.SAD;
        moodTimer = 0.9;
        invulnerableTimer = 1.4;
    }

    public void setFinalMood(Mood finalMood) {
        mood = finalMood;
        moodTimer = 1000;
    }

    public boolean isInvulnerable() {
        return invulnerableTimer > 0;
    }

    public Mood getMood() {
        return mood;
    }

    @Override
    public void draw(Graphics2D g) {
        boolean blink = invulnerableTimer > 0 && ((int) (invulnerableTimer * 12)) % 2 == 0;
        java.awt.Composite old = g.getComposite();
        java.awt.geom.AffineTransform oldTransform = g.getTransform();
        if (blink) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }
        double centerX = x + width / 2.0;
        double footY = y + height;
        double lift = 0;
        double tilt = 0;
        double scaleX = 1;
        double scaleY = 1 + Math.sin(idleTime * 3) * 0.012;
        if (walking) {
            double step = Math.abs(Math.sin(walkPhase));
            lift = step * height * 0.045;
            tilt = (facingRight ? 1 : -1) * 0.04 + Math.sin(walkPhase) * 0.05;
            scaleX = 1 + (1 - step) * 0.025;
            scaleY = 1 - (1 - step) * 0.035;
        }
        g.translate(centerX, footY - lift);
        g.rotate(tilt);
        g.scale(scaleX, scaleY);
        g.translate(-centerX, -footY);
        Sprite current = mood == Mood.LOVE ? love : mood == Mood.SAD ? sad : normal;
        current.drawFeet(g, centerX, footY, !facingRight);
        g.setTransform(oldTransform);
        g.setComposite(old);
    }
}
