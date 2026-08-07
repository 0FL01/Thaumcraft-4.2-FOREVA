package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventHandlerEntityCloneStaticGuardTest {

    @Test
    public void clonePathKeepsCapabilityFallbackCopyGuards() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue("Clone handler must read capability from the original player",
                source.contains("original.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null)"));
        assertTrue("Clone handler must fallback to authoritative cached/loaded research data when original cap is unavailable",
                source.contains("if (oldCap == null && original != null)")
                        && source.contains("oldCap = ResearchManager.getResearchData(original.getName());"));
        assertTrue("Clone handler must preserve capability copy flow",
                source.contains("newCap.deserializeNBT(oldCap.serializeNBT());")
                        && source.contains("newCap.setPlayer(clone);")
                        && source.contains("grantAutoUnlockResearch(clone);"));
        assertTrue("Auto-unlock research omitted from NBT must be restored silently after both load and clone",
                source.contains("grantAutoUnlockResearch(player);")
                        && source.contains("static void grantAutoUnlockResearch(EntityPlayer player)")
                        && source.contains("ri != null && ri.isAutoUnlock()")
                        && source.contains("knowledge.addResearch(ri.key);")
                        && source.contains("ResearchManager.updateCache(player.getName(), knowledge);"));
        int restoreStart = source.indexOf("static void grantAutoUnlockResearch(EntityPlayer player)");
        int restoreEnd = source.indexOf("\n    /**", restoreStart);
        assertTrue(restoreStart >= 0 && restoreEnd > restoreStart);
        String restore = source.substring(restoreStart, restoreEnd);
        assertFalse("Derived auto-unlock restoration must not emit completion feedback",
                restore.contains("ResearchManager.addResearch"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
