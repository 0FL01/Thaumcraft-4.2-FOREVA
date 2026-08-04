package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GolemActionSoundStaticGuardTest {

    @Test
    public void fishingUsesRegistered112BobberSounds() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/ai/interact/AIFish.java");
        assertTrue(source.contains("SoundEvents.ENTITY_BOBBER_THROW"));
        assertTrue(source.contains("SoundEvents.ENTITY_BOBBER_SPLASH"));
        assertFalse(source.contains("random.bow"));
        assertFalse(source.contains("random.splash"));
    }

    @Test
    public void fluidAndEssentiaTransfersUseRegistered112SwimSound() throws IOException {
        String[] paths = {
                "src/main/java/thaumcraft/common/entities/ai/fluid/AILiquidGather.java",
                "src/main/java/thaumcraft/common/entities/ai/fluid/AILiquidEmpty.java",
                "src/main/java/thaumcraft/common/entities/ai/fluid/AIEssentiaGather.java",
                "src/main/java/thaumcraft/common/entities/ai/fluid/AIEssentiaEmpty.java"
        };
        for (String path : paths) {
            String source = read(path);
            assertTrue(path, source.contains("SoundEvents.ENTITY_GENERIC_SWIM"));
            assertFalse(path, source.contains("game.neutral.swim"));
        }
    }

    @Test
    public void itemPickupUsesRegistered112Sound() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/ai/inventory/AIItemPickup.java");
        assertTrue(source.contains("SoundEvents.ENTITY_ITEM_PICKUP"));
        assertFalse(source.contains("random.pop"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
