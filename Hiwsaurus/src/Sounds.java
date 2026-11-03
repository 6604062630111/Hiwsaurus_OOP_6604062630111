import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public final class Sounds {
    public static final String EAT = "eat";
    public static final String BAD = "bad";
    public static final String BOSS_DIE = "boss_die";

    private static final Map<String, byte[]> DATA = new HashMap<>();
    private static boolean enabled = true;

    private Sounds() {
    }

    public static void play(String name) {
        if (!enabled) {
            return;
        }
        try {
            byte[] bytes = DATA.get(name);
            if (bytes == null) {
                bytes = Assets.bytes("sounds/" + name + ".wav");
                DATA.put(name, bytes);
            }
            AudioInputStream stream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.open(stream);
            clip.start();
        } catch (Exception | Error e) {
            enabled = false;
        }
    }
}
