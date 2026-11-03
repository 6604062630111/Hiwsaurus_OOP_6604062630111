import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class Assets {
    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private Assets() {
    }

    public static BufferedImage image(String path) {
        BufferedImage cached = CACHE.get(path);
        if (cached != null) {
            return cached;
        }
        BufferedImage loaded = copyOf(read(path));
        CACHE.put(path, loaded);
        return loaded;
    }

    public static byte[] bytes(String path) {
        try {
            InputStream resource = Assets.class.getResourceAsStream("/assets/" + path);
            if (resource == null) {
                File[] candidates = {new File("assets/" + path), new File("../assets/" + path)};
                for (File file : candidates) {
                    if (file.exists()) {
                        resource = new FileInputStream(file);
                        break;
                    }
                }
            }
            if (resource == null) {
                throw new RuntimeException("File not found: " + path);
            }
            try (InputStream in = resource) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                in.transferTo(out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + path, e);
        }
    }

    private static BufferedImage read(String path) {
        try {
            InputStream resource = Assets.class.getResourceAsStream("/assets/" + path);
            if (resource != null) {
                try (InputStream in = resource) {
                    return ImageIO.read(in);
                }
            }
            File[] candidates = {new File("assets/" + path), new File("../assets/" + path)};
            for (File file : candidates) {
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read image: " + path, e);
        }
        throw new RuntimeException("Image not found: " + path);
    }

    private static BufferedImage copyOf(BufferedImage source) {
        int type = source.getTransparency() == Transparency.OPAQUE
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), type);
        Graphics2D g = copy.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }
}
