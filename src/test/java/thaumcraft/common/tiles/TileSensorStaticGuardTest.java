package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileSensorStaticGuardTest {

    @Test
    public void tileSensorShouldKeepNoteToneAndSignalStateContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileSensor.java");

        assertTrue(source.contains("public byte note = 0;"));
        assertTrue(source.contains("public byte tone = 0;"));
        assertTrue(source.contains("public int redstoneSignal = 0;"));
        assertTrue(source.contains("compound.setByte(\"note\", this.note);"));
        assertTrue(source.contains("compound.setByte(\"tone\", this.tone);"));
        assertTrue(source.contains("this.note = compound.getByte(\"note\");"));
        assertTrue(source.contains("this.tone = compound.getByte(\"tone\");"));
        assertTrue(source.contains("if (this.note < 0)"));
        assertTrue(source.contains("if (this.note > 24)"));
    }

    @Test
    public void tileSensorShouldKeepReferenceNoteEventProcessingFlow() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileSensor.java");

        assertTrue(source.contains("if (this.redstoneSignal > 0)"));
        assertTrue(source.contains("if (this.redstoneSignal == 0)"));
        assertTrue(source.contains("noteBlockEvents.get((WorldServer) this.world)"));
        assertTrue(source.contains("if (data[3] != this.tone || data[4] != this.note)"));
        assertTrue(source.contains("getDistanceSq(data[0] + 0.5, data[1] + 0.5, data[2] + 0.5) <= 4096.0"));
        assertTrue(source.contains("this.triggerNote(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), false);"));
        assertTrue(source.contains("this.redstoneSignal = 10;"));
        assertTrue(source.contains("notifySignalChange();"));
    }

    @Test
    public void tileSensorShouldKeepToneAndTriggerNoteMaterialMapping() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileSensor.java");

        assertTrue(source.contains("if (materialBelow == Material.ROCK)"));
        assertTrue(source.contains("} else if (materialBelow == Material.GLASS)"));
        assertTrue(source.contains("} else if (materialBelow == Material.WOOD)"));
        assertTrue(source.contains("} else if (materialBelow == Material.SAND)"));
        assertTrue(source.contains("this.note = (byte) ((this.note + 1) % 25);"));
        assertTrue(source.contains("world.addBlockEvent(new BlockPos(x, y, z), ConfigBlocks.blockWoodenDevice, instrument, this.note);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
