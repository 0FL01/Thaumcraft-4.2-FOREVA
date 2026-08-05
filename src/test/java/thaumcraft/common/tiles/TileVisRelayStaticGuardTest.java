package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TileVisRelayStaticGuardTest {

    @Test
    public void visRelayShouldKeepReferenceWandCycleContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileVisRelay.java");

        assertTrue(source.contains("public byte orientation = 1;"));
        assertTrue(source.contains("this.setRelayColor(nextColor);"));
        assertTrue(source.contains("this.removeThisNode();"));
        assertTrue(source.contains("this.world.playSound(null, this.pos, TCSounds.CRYSTAL, SoundCategory.BLOCKS, 0.2F, 1.0F);"));
        assertTrue(source.contains("public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player)"));
        assertTrue(source.contains("return null;"));
    }

    @Test
    public void chunkUnloadMustNotTearDownTheVisGraph() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileVisRelay.java");
        int start = source.indexOf("public void onChunkUnload()");
        int end = source.indexOf("\n    }", start);

        assertTrue(start >= 0 && end > start);
        assertFalse(source.substring(start, end).contains("removeThisNode"));
    }

    @Test
    public void visRelayShouldKeepReferenceChannelSyncAndPulseLifecycle() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileVisRelay.java");

        assertTrue(source.contains("protected Object beam1;"));
        assertTrue(source.contains("this.drawEffect();"));
        assertTrue(source.contains("Thaumcraft.proxy.beamPower(this.world,"));
        assertTrue(source.contains("this.pulse > 0, this.beam1"));
        assertTrue(source.contains("this.world.addBlockEvent(this.pos"));
        assertTrue(source.contains("public boolean receiveClientEvent(int id, int type)"));
        assertTrue(source.contains("relayParent.get() instanceof TileVisRelay"));
        assertTrue(source.contains("nbt.setByte(\"px\""));
        assertTrue(source.contains("this.px = nbt.getByte(\"px\");"));
        assertTrue(source.contains("this.parentLoaded = true;"));
        assertTrue(source.contains("this.beam1 = null;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
