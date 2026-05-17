package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityPrimalArrowStaticGuardTest {

    @Test
    public void arrowEventsShouldKeepPrimalArrowLaunchContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("findPrimalArrowType(player)"));
        assertTrue(source.contains("new EntityPrimalArrow(player.world, player, velocityFactor * velocityMultiplier, primalType)"));
        assertTrue(source.contains("InventoryUtils.consumeInventoryItem(player, ConfigItems.itemPrimalArrow, primalType)"));
        assertTrue(source.contains("event.setAction(new ActionResult<>(EnumActionResult.SUCCESS, bow));"));
        assertTrue(source.contains("event.setCanceled(true);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
