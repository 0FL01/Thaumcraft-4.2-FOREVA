package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityHoverBoundsStaticGuardTest {

    @Test
    public void livingUpdateShouldEnforceOuterLandsHoverFlightBounds() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue(source.contains("enforceHoverFlightBounds(player);"));
        assertTrue(source.contains("private void enforceHoverFlightBounds(EntityPlayer player)"));
        assertTrue(source.contains("player.world.provider.getDimension() == Config.dimensionOuterId"));
        assertTrue(source.contains("Hover.setHover(player.getEntityId(), false);"));
        assertTrue(source.contains("player.sendMessage(new TextComponentTranslation(\"tc.break.fly\"));"));
        assertTrue(source.contains("chest.getItem() != ConfigItems.itemHoverHarness"));
        assertTrue(lang.contains("tc.break.fly=Something disrupts your ability to fly."));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
