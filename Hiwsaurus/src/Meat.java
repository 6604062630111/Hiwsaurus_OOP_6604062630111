public class Meat extends Collectible {
    public static final int POINTS = 10;
    public static final int BOSS_DAMAGE = 6;

    public Meat(Sprite sprite, double centerX, double startY, double speed) {
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
