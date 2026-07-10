package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceGrateContractTest {

    @Test
    public void grateBundleShouldKeepOpenClosedToggleCollisionAndDropContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");

        assertTrue("Closed grate should drop the open grate item and the creative tab should expose only meta 5",
                source.contains("if (meta == 6) return 5;")
                        && source.contains("list.add(new ItemStack(this, 1, 5)); // grate")
                        && !source.contains("list.add(new ItemStack(this, 1, 6)); // grate"));

        assertTrue("BlockMetalDevice should keep hand and redstone grate toggles plus thin collision bounds",
                source.contains("if (state.getValue(TYPE) == 5) {")
                        && source.contains("toggleGrate(worldIn, pos, state, 6, playerIn);")
                        && source.contains("if (state.getValue(TYPE) == 6) {")
                        && source.contains("toggleGrate(worldIn, pos, state, 5, playerIn);")
                        && source.contains("boolean powered = worldIn.isBlockPowered(pos);")
                        && source.contains("onPoweredBlockChange(worldIn, pos, powered);")
                        && source.contains("if (meta == 5 && powered) {")
                        && source.contains("state.withProperty(TYPE, 6)")
                        && source.contains("if (meta == 6 && !powered) {")
                        && source.contains("state.withProperty(TYPE, 5)")
                        && source.contains("private static final AxisAlignedBB GRATE_AABB")
                        && source.contains("return (meta == 5 || meta == 6) ? GRATE_AABB : FULL_BLOCK_AABB;")
                        && source.contains("if (!(entityIn instanceof EntityItem)) {")
                        && source.contains("addCollisionBoxToList(pos, entityBox, collidingBoxes, GRATE_AABB);"));
    }

    @Test
    public void grateBlockModelsShouldUseThinShellsInsteadOfCubeAllPlaceholders() throws IOException {
        String openModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_5.json");
        String closedModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_6.json");

        assertTrue("Open grate model should now be a thin top slab with grate and metal textures",
                openModel.contains("\"ambientocclusion\": false")
                        && openModel.contains("\"surface\": \"thaumcraft:blocks/grate\"")
                        && openModel.contains("\"side\": \"thaumcraft:blocks/metalbase\"")
                        && openModel.contains("\"from\": [0, 13, 0]")
                        && openModel.contains("\"to\": [16, 16, 16]")
                        && !openModel.contains("\"parent\": \"block/cube_all\""));

        assertTrue("Closed grate model should keep the same slab plus an inset hatch layer",
                closedModel.contains("\"ambientocclusion\": false")
                        && closedModel.contains("\"hatch\": \"thaumcraft:blocks/grate_hatch\"")
                        && closedModel.contains("\"from\": [1, 14, 1]")
                        && closedModel.contains("\"to\": [15, 15, 15]")
                        && !closedModel.contains("\"parent\": \"block/cube_all\""));
    }

    @Test
    public void grateItemShouldUseTheLoweredTwoPassTc4InventoryShell() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_5_inventory.json");

        assertTrue("Open and internal closed grate stacks should use the dedicated inventory model without changing world models",
                clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 5, \"blockmetaldevice_5_inventory\");")
                        && clientProxy.contains("registerBuiltinItemModel(metalDeviceItem, 6, \"blockmetaldevice_5_inventory\");"));
        assertTrue("Grate inventory model should preserve the original -0.3 Y lowering and grate/hatch render passes",
                itemModel.contains("\"parent\": \"block/block\"")
                        && itemModel.contains("\"surface\": \"thaumcraft:blocks/grate\"")
                        && itemModel.contains("\"hatch\": \"thaumcraft:blocks/grate_hatch\"")
                        && itemModel.contains("\"from\": [0, 8.2, 0]")
                        && itemModel.contains("\"to\": [16, 11.2, 16]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
