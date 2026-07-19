package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WarpLifecycleStaticGuardTest {

    @Test
    public void playerTickKeepsOriginalWarpCadenceAndSuppression() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("!Config.wuss && player.ticksExisted > 0 && player.ticksExisted % 2000 == 0"));
        assertTrue(source.contains("!player.isPotionActive(Config.potionWarpWard)"));
        assertTrue(source.contains("player.ticksExisted % 10 == 0 && player.isPotionActive(Config.potionDeathGaze)"));
        assertFalse(source.contains("knowledge.setWarpCounter(0);"));
    }

    @Test
    public void acceptedWarpChangesArmCounterBeforeSync() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");

        assertTrue(source.contains("player == null || player.world.isRemote || amount == 0"));
        assertTrue(count(source, "knowledge.setWarpCounter(knowledge.getTotalWarp());") == 2);
        assertTrue(count(source, "ResearchManager.syncWarp(player);") == 2);
        assertTrue(source.indexOf("knowledge.setWarpCounter(knowledge.getTotalWarp());")
                < source.indexOf("ResearchManager.syncWarp(player);"));
    }

    private static int count(String source, String needle) {
        int count = 0;
        int offset = 0;
        while ((offset = source.indexOf(needle, offset)) >= 0) {
            count++;
            offset += needle.length();
        }
        return count;
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
