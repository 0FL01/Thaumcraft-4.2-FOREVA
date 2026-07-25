package thaumcraft.rendering;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.BlockRenderLayer;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.blocks.BlockCustomOre;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InfusedOreRenderingContractTest {
    private static final Path ASSET_ROOT = Paths.get("src/main/resources/assets/thaumcraft");
    private static final String[] FACES = {"down", "up", "north", "south", "west", "east"};

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void originalAnimatedOverlayComplementsTheStoneBase() throws IOException {
        BufferedImage base = ImageIO.read(ASSET_ROOT.resolve("textures/blocks/infusedorestone.png").toFile());
        BufferedImage overlay = ImageIO.read(ASSET_ROOT.resolve("textures/blocks/infusedore.png").toFile());
        assertNotNull(base);
        assertNotNull(overlay);
        assertEquals(32, base.getWidth());
        assertEquals(32, base.getHeight());
        assertEquals(32, overlay.getWidth());
        assertEquals(512, overlay.getHeight());

        for (int frame = 0; frame < 16; frame++) {
            for (int y = 0; y < 32; y++) {
                for (int x = 0; x < 32; x++) {
                    int baseAlpha = base.getRGB(x, y) >>> 24;
                    int overlayAlpha = overlay.getRGB(x, frame * 32 + y) >>> 24;
                    assertTrue(baseAlpha == 0 || baseAlpha == 255);
                    assertTrue(overlayAlpha == 0 || overlayAlpha == 255);
                    assertTrue("Gap or overlap in frame " + frame + " at " + x + "," + y,
                            (baseAlpha == 255) != (overlayAlpha == 255));
                }
            }
        }

        JsonObject animation = parse(ASSET_ROOT.resolve("textures/blocks/infusedore.png.mcmeta"))
                .getAsJsonObject("animation");
        assertEquals(2, animation.get("frametime").getAsInt());
    }

    @Test
    public void infusedMetasShareOneUntintedBaseAndTintedOverlayModel() throws IOException {
        JsonObject model = parse(ASSET_ROOT.resolve("models/block/blockcustomore_infused.json"));
        JsonObject textures = model.getAsJsonObject("textures");
        assertEquals("thaumcraft:blocks/infusedorestone", textures.get("particle").getAsString());
        assertEquals("thaumcraft:blocks/infusedorestone", textures.get("base").getAsString());
        assertEquals("thaumcraft:blocks/infusedore", textures.get("overlay").getAsString());

        JsonArray elements = model.getAsJsonArray("elements");
        assertEquals(2, elements.size());
        for (int elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
            JsonObject element = elements.get(elementIndex).getAsJsonObject();
            assertEquals("[0,0,0]", element.getAsJsonArray("from").toString());
            assertEquals("[16,16,16]", element.getAsJsonArray("to").toString());
            JsonObject faces = element.getAsJsonObject("faces");
            assertEquals(6, faces.entrySet().size());
            for (String direction : FACES) {
                JsonObject face = faces.getAsJsonObject(direction);
                assertEquals(elementIndex == 0 ? "#base" : "#overlay", face.get("texture").getAsString());
                assertEquals(direction, face.get("cullface").getAsString());
                if (elementIndex == 0) {
                    assertFalse(face.has("tintindex"));
                } else {
                    assertEquals(0, face.get("tintindex").getAsInt());
                }
            }
        }

        JsonObject variants = parse(ASSET_ROOT.resolve("blockstates/blockcustomore.json"))
                .getAsJsonObject("variants");
        assertEquals("thaumcraft:blockcustomore_0", variants.getAsJsonObject("type=0").get("model").getAsString());
        for (int meta = 1; meta <= 6; meta++) {
            assertEquals("thaumcraft:blockcustomore_infused",
                    variants.getAsJsonObject("type=" + meta).get("model").getAsString());
        }
        assertEquals("thaumcraft:blockcustomore_7", variants.getAsJsonObject("type=7").get("model").getAsString());
    }

    @Test
    public void onlyInfusedMetasUseCutoutMippedRendering() {
        BlockCustomOre ore = new BlockCustomOre();
        for (int meta = 0; meta <= 7; meta++) {
            for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                boolean expected = meta >= 1 && meta <= 6
                        ? layer == BlockRenderLayer.CUTOUT_MIPPED
                        : layer == BlockRenderLayer.SOLID;
                assertEquals("Unexpected layer for ore meta " + meta + ": " + layer, expected,
                        ore.canRenderInLayer(ore.getStateFromMeta(meta), layer));
            }
        }
    }

    @Test
    public void clientRegistersBoundedBlockAndItemOverlayTints() throws IOException {
        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        assertTrue(proxy.contains("state.getValue(BlockCustomOre.TYPE)"));
        assertTrue(proxy.contains("int meta = stack.getMetadata();"));
        assertEquals(2, occurrences(proxy, "BlockCustomOreItem.colors[meta]"));
        assertTrue(occurrences(proxy, "tintIndex == 0 && meta >= 1 && meta <= 6") >= 2);
    }

    private static int occurrences(String value, String needle) {
        int count = 0;
        for (int index = 0; (index = value.indexOf(needle, index)) >= 0; index += needle.length()) {
            count++;
        }
        return count;
    }

    private static JsonObject parse(Path path) throws IOException {
        return new JsonParser().parse(read(path)).getAsJsonObject();
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
