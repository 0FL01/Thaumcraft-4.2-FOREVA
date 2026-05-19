package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceBrainboxContractTest {

    @Test
    public void blockMetalDeviceAndConfigBlocksWireBrainboxMetadataAndTileRegistration() throws IOException {
        String metalDevice = readFile("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
        String configBlocks = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String metalDeviceBlockstate = readFile("src/main/resources/assets/thaumcraft/blockstates/blockmetaldevice.json");
        String metalDeviceBrainboxModel = readFile("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_12.json");

        assertTrue("BlockMetalDevice must create TileBrainbox for metadata 12",
                metalDevice.contains("if (meta == 12) return new TileBrainbox();"));
        assertTrue("BlockMetalDevice creative/meta list must include metadata 12",
                metalDevice.contains("list.add(new ItemStack(this, 1, 12));"));
        assertTrue("BlockMetalDevice should set brainbox facing when placed",
                metalDevice.contains("if (worldIn.isRemote || state.getValue(TYPE) != 12) return;"));
        assertTrue("ConfigBlocks must register TileBrainbox tile entity",
                configBlocks.contains("new TileRegistration(TileBrainbox.class, \"TileBrainbox\")"));
        assertTrue("blockmetaldevice blockstate must map metadata 12 to dedicated brainbox model",
                metalDeviceBlockstate.contains("\"type=12\": { \"model\": \"thaumcraft:blockmetaldevice_12\" }"));
        assertTrue("Dedicated brainbox model must preserve the reference-sized center core instead of a full cube",
                metalDeviceBrainboxModel.contains("\"from\": [3, 3, 3]")
                        && metalDeviceBrainboxModel.contains("\"to\": [13, 13, 13]")
                        && metalDeviceBrainboxModel.contains("\"brainbox\": \"thaumcraft:blocks/brainbox\""));
        assertTrue("Brainbox texture asset must exist in the port resource tree",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/brainbox.png")));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
