package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceBrainboxContractTest {

    @Test
    public void blockMetalDeviceAndConfigBlocksWireBrainboxMetadataAndTileRegistration() throws IOException {
        String metalDevice = readFile("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
        String configBlocks = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");

        assertTrue("BlockMetalDevice must create TileBrainbox for metadata 12",
                metalDevice.contains("if (meta == 12) return new TileBrainbox();"));
        assertTrue("BlockMetalDevice creative/meta list must include metadata 12",
                metalDevice.contains("list.add(new ItemStack(this, 1, 12));"));
        assertTrue("BlockMetalDevice should set brainbox facing when placed",
                metalDevice.contains("if (worldIn.isRemote || state.getValue(TYPE) != 12) return;"));
        assertTrue("ConfigBlocks must register TileBrainbox tile entity",
                configBlocks.contains("new TileRegistration(TileBrainbox.class, \"TileBrainbox\")"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
