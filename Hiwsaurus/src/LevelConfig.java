public class LevelConfig {
    private static final LevelConfig[] ALL = {
            new LevelConfig(1, 1, 150, 0, 250, 320, 0.90, 0.14, 720),
            new LevelConfig(1, 2, 240, 0, 290, 370, 0.84, 0.20, 720),
            new LevelConfig(1, 3, 0, 110, 300, 380, 0.78, 0.20, 720),
            new LevelConfig(2, 1, 320, 0, 340, 430, 0.74, 0.24, 780),
            new LevelConfig(2, 2, 400, 0, 370, 470, 0.68, 0.27, 780),
            new LevelConfig(2, 3, 0, 170, 380, 480, 0.66, 0.26, 780),
            new LevelConfig(3, 1, 480, 0, 420, 520, 0.62, 0.30, 860),
            new LevelConfig(3, 2, 580, 0, 450, 560, 0.58, 0.33, 860),
            new LevelConfig(3, 3, 0, 210, 430, 530, 0.62, 0.22, 880)
    };

    private final int chapter;
    private final int level;
    private final int targetPoints;
    private final int bossHp;
    private final double minSpeed;
    private final double maxSpeed;
    private final double spawnInterval;
    private final double vegetableChance;
    private final double playerSpeed;

    private LevelConfig(int chapter, int level, int targetPoints, int bossHp, double minSpeed,
                        double maxSpeed, double spawnInterval, double vegetableChance, double playerSpeed) {
        this.chapter = chapter;
        this.level = level;
        this.targetPoints = targetPoints;
        this.bossHp = bossHp;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.spawnInterval = spawnInterval;
        this.vegetableChance = vegetableChance;
        this.playerSpeed = playerSpeed;
    }

    public static LevelConfig get(int chapter, int level) {
        return ALL[(chapter - 1) * Const.LEVELS_PER_CHAPTER + (level - 1)];
    }

    public static LevelConfig next(LevelConfig current) {
        int index = (current.chapter - 1) * Const.LEVELS_PER_CHAPTER + (current.level - 1) + 1;
        return index < ALL.length ? ALL[index] : null;
    }

    public int getChapter() {
        return chapter;
    }

    public int getLevel() {
        return level;
    }

    public int getTargetPoints() {
        return targetPoints;
    }

    public int getBossHp() {
        return bossHp;
    }

    public boolean isBoss() {
        return bossHp > 0;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getSpawnInterval() {
        return spawnInterval;
    }

    public double getVegetableChance() {
        return vegetableChance;
    }

    public double getPlayerSpeed() {
        return playerSpeed;
    }

    public double getBossSpeed() {
        return 150 + chapter * 60;
    }

    public double getWheyChance() {
        return 0.08;
    }

    public double getEquipmentChance() {
        return 0.12;
    }
}
