package thaumcraft.common.entities.ai.pech;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AIPechPickupSoundStaticGuardTest {

    @Test
    public void successfulPickupUsesTheRegistered112ItemSound() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/entities/ai/pech/AIPechItemEntityGoto.java")),
                StandardCharsets.UTF_8);

        assertTrue(source.contains("SoundEvents.ENTITY_ITEM_PICKUP"));
        assertFalse(source.contains("random.pop"));
        assertFalse(source.contains("SoundEvent.REGISTRY.getObject"));
    }
}
