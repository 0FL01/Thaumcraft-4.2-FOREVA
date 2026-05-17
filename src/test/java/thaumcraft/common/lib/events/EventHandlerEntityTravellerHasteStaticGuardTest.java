package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityTravellerHasteStaticGuardTest {

    @Test
    public void livingUpdateShouldApplyTravellerBootsHasteMovementBonus() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("applyTravellerHasteMovement(player);"));
        assertTrue(source.contains("private void applyTravellerHasteMovement(EntityPlayer player)"));
        assertTrue(source.contains("Config.enchHaste == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Config.enchHaste, boots)"));
        assertTrue(source.contains("player.moveRelative(0.0F, 0.0F, 1.0F, bonus);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
