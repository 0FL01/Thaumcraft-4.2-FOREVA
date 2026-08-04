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
        String hover = readFile("src/main/java/thaumcraft/common/items/armor/Hover.java");

        assertTrue(source.contains("applyTravellerHasteMovement(player);"));
        assertTrue(source.indexOf("applyTravellerHasteMovement(player);")
                < source.indexOf("if (living.world.isRemote)"));
        assertTrue(source.contains("private void applyTravellerHasteMovement(EntityPlayer player)"));
        assertTrue(source.contains("Config.enchHaste == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Config.enchHaste, boots)"));
        assertTrue(source.contains("player.moveRelative(0.0F, 0.0F, 1.0F, bonus);"));
        assertTrue(!source.contains("boots.getItem() != ConfigItems.itemBootsTraveller || player.moveForward"));
        assertTrue(source.contains("player.isSneaking() || boots.isEmpty() || boots.getItem() != ConfigItems.itemBootsTraveller")
                && source.contains("Hover.resetHover(player);"));
        assertTrue(!hover.contains("else if (player.stepHeight > 0.5F)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
