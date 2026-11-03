public class Vegetable extends Collectible {
    public Vegetable(Sprite sprite, double centerX, double startY, double speed) {
        super(sprite, centerX, startY, speed);
    }

    @Override
    protected double hitboxScale() {
        return 0.6;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public int getBossDamage() {
        return 0;
    }

    @Override
    public boolean isHarmful() {
        return true;
    }
}
