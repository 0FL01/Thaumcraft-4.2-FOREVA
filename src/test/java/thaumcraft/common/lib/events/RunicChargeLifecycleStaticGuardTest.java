package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RunicChargeLifecycleStaticGuardTest {
    @Test
    public void runicChargeLivesOnlyInTheEventHandlerAndLivePacket() throws IOException {
        String runic = read("src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java");
        String entities = read("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");
        String knowledge = read("src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java");

        String setter = between(runic, "private void setRunicCharge", "private void syncRunicCharge");
        assertTrue(setter.contains("this.runicCharge.put(player.getEntityId(), safeCharge);"));
        assertFalse(setter.contains("PlayerKnowledgeProvider") || setter.contains("setRunicCharge(safeCharge)"));
        assertTrue(entities.contains("runicEventHandler.runicCharge.remove(player.getEntityId())")
                && entities.contains("handler.runicCharge.get(player.getEntityId())"));
        assertFalse(entities.contains("runicCharge.put(player.getEntityId(), knowledge.getRunicCharge())")
                || entities.contains("knowledge.getRunicCharge(), max"));
        assertFalse(knowledge.contains("setInteger(TAG_RUNIC_CHARGE")
                || knowledge.contains("getInteger(TAG_RUNIC_CHARGE"));
        assertTrue(knowledge.contains("runicCharge = 0;"));
    }

    private static String between(String source, String start, String end) {
        int from = source.indexOf(start);
        int to = source.indexOf(end, from);
        assertTrue(from >= 0 && to > from);
        return source.substring(from, to);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
