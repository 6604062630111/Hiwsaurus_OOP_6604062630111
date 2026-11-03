import javax.swing.JFrame;
import java.awt.Dimension;

public class GameFrame extends JFrame {
    public GameFrame() {
        super("Hiwsaurus");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        GamePanel panel = new GamePanel();
        panel.setPreferredSize(new Dimension(1152, 648));
        setContentPane(panel);
        pack();
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(null);
        setIconImage(Assets.image("player/MC1.png"));
        panel.start();
    }
}
