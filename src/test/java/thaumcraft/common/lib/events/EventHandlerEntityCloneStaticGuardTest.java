package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
                        && source.contains("ResearchManager.updateCache(clone.getName(), newCap);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
