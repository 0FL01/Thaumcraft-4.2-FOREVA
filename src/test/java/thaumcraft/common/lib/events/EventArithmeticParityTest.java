package thaumcraft.common.lib.events;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import thaumcraft.common.lib.world.dim.Cell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventArithmeticParityTest {

    @Test
    public void runicRechargeKeepsSignedTc4Interval() {
        assertEquals(2000L, EventHandlerRunic.rechargeInterval(2000, 0));
        assertEquals(0L, EventHandlerRunic.rechargeInterval(2000, 4));
        assertEquals(-500L, EventHandlerRunic.rechargeInterval(2000, 5));
    }

    @Test
    public void bossPlacementGateAcceptsOnlyBossRoomFeatures() {
        assertFalse(EventHandlerWorld.isBossPlacementCell(null));
        for (int feature = 0; feature <= 8; feature++) {
            Cell cell = new Cell();
            cell.feature = (byte) feature;
            assertEquals(feature >= 2 && feature <= 5,
                    EventHandlerWorld.isBossPlacementCell(cell));
        }
    }

    @Test
    public void bossSearchUsesPlacementChunkAndPosition() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java");
        assertTrue(source.contains("new CellLoc(pos.getX() >> 4, pos.getZ() >> 4)"));
        assertTrue(source.contains("pos.getX(), pos.getY(), pos.getZ(), null, EntityThaumcraftBoss.class, 32.0"));
        assertFalse(source.contains("player.getEntityBoundingBox().grow(32.0)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
