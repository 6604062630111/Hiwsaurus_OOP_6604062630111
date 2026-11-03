import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.ByteArrayInputStream;

public final class Music {
    public static final String MENU = "bgm_menu";
    public static final String PLAY = "bgm_play";
    public static final String BOSS = "bgm_boss";

    private static Clip clip;
    private static String current;
    private static boolean enabled = true;

    private Music() {
    }

    public static void play(String name) {
        if (!enabled) {
            return;
        }
        if (name == null) {
            stop();
            return;
        }
        if (name.equals(current) && clip != null && clip.isRunning()) {
            return;
        }
        stop();
        try {
            byte[] bytes = Assets.bytes("sounds/" + name + ".wav");
            AudioInputStream stream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
            Clip next = AudioSystem.getClip();
            next.open(stream);
            if (next.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) next.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(Math.max(gain.getMinimum(), -9.0f));
            }
            next.loop(Clip.LOOP_CONTINUOUSLY);
            clip = next;
            current = name;
        } catch (Exception | Error e) {
            enabled = false;
        }
    }

    public static void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        current = null;
    }
}
