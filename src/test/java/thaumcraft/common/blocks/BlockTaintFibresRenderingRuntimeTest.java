package thaumcraft.common.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.BlockRenderLayer;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigBlocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockTaintFibresRenderingRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockTaintFibres == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void tc4FibresUseTranslucentRenderingForTheirPartialAlphaTexture() {
        assertEquals(BlockRenderLayer.TRANSLUCENT, ConfigBlocks.blockTaintFibres.getRenderLayer());
        assertTrue(ConfigBlocks.blockTaintFibres.canRenderInLayer(
                ConfigBlocks.blockTaintFibres.getDefaultState(), BlockRenderLayer.TRANSLUCENT));
        assertFalse(ConfigBlocks.blockTaintFibres.canRenderInLayer(
                ConfigBlocks.blockTaintFibres.getDefaultState(), BlockRenderLayer.CUTOUT_MIPPED));
    }

    @Test
    public void surfaceModelsComposeWithEveryTc4GrowthType() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(
                "src/main/resources/assets/thaumcraft/blockstates/blocktaintfibres.json")),
                StandardCharsets.UTF_8);
        JsonArray multipart = new JsonParser().parse(json).getAsJsonObject().getAsJsonArray("multipart");
        assertEquals(10, multipart.size());

        String[] faces = {"down", "up", "north", "south", "west", "east"};
        for (String face : faces) {
            assertEquals(1, countParts(multipart, face, "true",
                    "thaumcraft:blocktaintfibres_surface_" + face));
        }
        for (int type = 1; type <= 4; type++) {
            assertEquals(1, countParts(multipart, "type", Integer.toString(type),
                    "thaumcraft:blocktaintfibres_" + type));
        }
    }

    private static int countParts(JsonArray multipart, String property, String value, String model) {
        int matches = 0;
        for (int i = 0; i < multipart.size(); i++) {
            JsonObject part = multipart.get(i).getAsJsonObject();
            JsonObject when = part.getAsJsonObject("when");
            if (model.equals(part.getAsJsonObject("apply").get("model").getAsString())) {
                assertEquals(1, when.entrySet().size());
                assertTrue(when.has(property));
                assertEquals(value, when.get(property).getAsString());
                matches++;
            }
        }
        return matches;
    }
}
