package thaumcraft.client;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InfusedOreTextureAlphaContractTest {
    private static final String[] ASPECTS = {"air", "fire", "water", "earth", "order", "entropy"};

    @Test
    public void compositeInfusedOreTexturesStayOpaqueInItemRendering() throws IOException {
        Path textureRoot = Paths.get("src/main/resources/assets/thaumcraft/textures/blocks");
        for (String aspect : ASPECTS) {
            BufferedImage image = ImageIO.read(textureRoot.resolve("infusedorestone_" + aspect + ".png").toFile());
            assertNotNull("Unreadable infused ore texture for " + aspect, image);
            assertEquals(32, image.getWidth());
            assertEquals(32, image.getHeight());
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    assertEquals("Translucent pixel in " + aspect + " at " + x + "," + y,
                            255, image.getRGB(x, y) >>> 24);
                }
            }
        }
    }
}
