import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class ItemFactory {
    private static final String[] MEATS = {"meat_steak", "meat_drumstick", "meat_leg", "meat_ribs",
            "meat_roast", "fish", "meat_slice"};
    private static final String[] VEGETABLES = {"carrot", "broccoli", "cabbage", "tomato", "corn", "pumpkin"};
    private static final String[] EQUIPMENT = {"kettlebell", "barbell_small", "barbell_large"};
    private static final Map<String, Sprite> SPRITES = new HashMap<>();
    private static final Random RANDOM = new Random();

    private ItemFactory() {
    }

    private static Sprite sprite(String name, double maxSide) {
        String key = name + "@" + maxSide;
        Sprite cached = SPRITES.get(key);
        if (cached == null) {
            cached = Sprite.fitting(Assets.image("items/" + name + ".png"), maxSide);
            SPRITES.put(key, cached);
        }
        return cached;
    }

    public static Collectible createMeat(double centerX, double startY, double speed) {
        String name = MEATS[RANDOM.nextInt(MEATS.length)];
        return new Meat(sprite(name, 104), centerX, startY, speed);
    }

    public static Collectible createVegetable(double centerX, double startY, double speed) {
        String name = VEGETABLES[RANDOM.nextInt(VEGETABLES.length)];
        return new Vegetable(sprite(name, 98), centerX, startY, speed);
    }

    public static Collectible createEquipment(double centerX, double startY, double speed) {
        String name = EQUIPMENT[RANDOM.nextInt(EQUIPMENT.length)];
        double size = name.equals("barbell_large") ? 150 : 100;
        return new Equipment(sprite(name, size), centerX, startY, speed);
    }

    public static Collectible createWhey(double centerX, double startY, double speed) {
        return new Whey(sprite("whey", 116), centerX, startY, speed);
    }
}
