public class Whey extends Collectible {
    public static final int POINTS = 30;
    public static final int BOSS_DAMAGE = 15;

    public Whey(Sprite sprite, double centerX, double startY, double speed) {
        super(sprite, centerX, startY, speed);
    }

    @Override
    public int getPoints() {
        return POINTS;
    }

    @Override
    public int getBossDamage() {
        return BOSS_DAMAGE;
    }

    @Override
    public boolean isHarmful() {
        return false;
    }
}
