package thaumcraft.client;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RotaryMachineShellContractTest {

    @Test
    public void centrifugeShellLivesInBlockModelWhileTesrKeepsOnlyRotaryCore() throws IOException {
        String centrifugeRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileCentrifugeRenderer.java");
        String centrifugeModel = read("src/main/java/thaumcraft/client/renderers/models/ModelCentrifuge.java");
        String centrifugeBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_2.json");
        String centrifugeItemModel = read("src/main/resources/assets/thaumcraft/models/item/blocktube_2_inventory.json");

        assertTrue("TileCentrifugeRenderer must keep the animated rotary core path without re-rendering the static box shell",
                centrifugeRenderer.contains("new ModelCentrifuge()")
                        && centrifugeRenderer.contains("GlStateManager.rotate(spin, 0.0F, 1.0F, 0.0F);")
                        && centrifugeRenderer.contains("model.renderSpinnyBit(MODEL_SCALE)")
                        && !centrifugeRenderer.contains("model.renderBoxes(MODEL_SCALE)"));
        assertTrue("ModelCentrifuge should still expose separate shell and rotary core parts",
                centrifugeModel.contains("crossbar")
                        && centrifugeModel.contains("dingus1")
                        && centrifugeModel.contains("dingus2")
                        && centrifugeModel.contains("core")
                        && centrifugeModel.contains("top")
                        && centrifugeModel.contains("bottom")
                        && centrifugeModel.contains("renderBoxes(float scale)")
                        && centrifugeModel.contains("renderSpinnyBit(float scale)"));
        assertTrue("Centrifuge block model should carry the top and bottom shell slabs instead of the old cube_all placeholder",
                centrifugeBlockModel.contains("\"ambientocclusion\": false")
                        && centrifugeBlockModel.contains("\"shell\": \"thaumcraft:models/centrifuge\"")
                        && centrifugeBlockModel.contains("\"from\": [4, 0, 4]")
                        && centrifugeBlockModel.contains("\"to\": [12, 4, 12]")
                        && centrifugeBlockModel.contains("\"from\": [4, 12, 4]")
                        && centrifugeBlockModel.contains("\"to\": [12, 16, 12]"));
        assertTrue("Centrifuge item model must bake all six TC4 ModelCentrifuge parts with the square atlas",
                centrifugeItemModel.contains("\"surface\": \"thaumcraft:models/centrifuge_inventory\"")
                        && centrifugeItemModel.contains("\"from\": [4, 7, 7]")
                        && centrifugeItemModel.contains("\"from\": [12, 5, 6]")
                        && centrifugeItemModel.contains("\"from\": [0, 5, 6]")
                        && centrifugeItemModel.contains("\"from\": [6.5, 4, 6.5]")
                        && centrifugeItemModel.contains("\"from\": [4, 12, 4]")
                        && centrifugeItemModel.contains("\"from\": [4, 0, 4]")
                        && centrifugeItemModel.contains("\"uv\": [4.5, 0, 6.5, 1]")
                        && centrifugeItemModel.contains("\"uv\": [2, 8, 3, 10]")
                        && centrifugeItemModel.contains("\"uv\": [0.75, 1.5, 1.5, 5.5]")
                        && centrifugeItemModel.contains("\"uv\": [11, 12, 13, 14]")
                        && centrifugeItemModel.contains("\"thirdperson_righthand\"")
                        && centrifugeItemModel.contains("\"firstperson_lefthand\""));
        assertEquals("Centrifuge item model must contain exactly the six TC4 boxes",
                6, occurrences(centrifugeItemModel, "\"from\""));
        assertEquals("Every face on all six centrifuge boxes must declare an explicit UV",
                36, occurrences(centrifugeItemModel, "\"uv\""));
    }

    @Test
    public void centrifugeInventoryTextureIsNearestNeighborSquareCopy() throws IOException {
        BufferedImage tc4 = ImageIO.read(new File("thaumcraft_src/assets/thaumcraft/textures/models/centrifuge.png"));
        BufferedImage source = ImageIO.read(new File("src/main/resources/assets/thaumcraft/textures/models/centrifuge.png"));
        BufferedImage inventory = ImageIO.read(new File("src/main/resources/assets/thaumcraft/textures/models/centrifuge_inventory.png"));

        assertNotNull(tc4);
        assertNotNull(source);
        assertNotNull(inventory);
        assertEquals(64, source.getWidth());
        assertEquals(32, source.getHeight());
        assertEquals(64, inventory.getWidth());
        assertEquals(64, inventory.getHeight());
        for (int y = 0; y < inventory.getHeight(); y++) {
            for (int x = 0; x < inventory.getWidth(); x++) {
                assertEquals("Port source must preserve the TC4 texture at " + x + "," + (y / 2),
                        tc4.getRGB(x, y / 2), source.getRGB(x, y / 2));
                assertEquals("Inventory atlas must use nearest-neighbor scaling at " + x + "," + y,
                        source.getRGB(x, y / 2), inventory.getRGB(x, y));
            }
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int occurrences(String value, String marker) {
        int count = 0;
        for (int offset = 0; (offset = value.indexOf(marker, offset)) >= 0; offset += marker.length()) {
            count++;
        }
        return count;
    }
}
