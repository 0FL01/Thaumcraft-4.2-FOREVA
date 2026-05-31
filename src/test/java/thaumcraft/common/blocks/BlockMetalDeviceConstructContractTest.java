package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceConstructContractTest {

    @Test
    public void blockMetalDeviceShouldKeepConstructThaumatoriumTileAndBoundsContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockmetaldevice.json");
        String advancedConstructModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_3.json");
        String constructModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_9.json");

        assertTrue("creative exposure should keep construct-family items and not expose thaumatorium multiblock halves directly",
                source.contains("list.add(new ItemStack(this, 1, 13)); // fertility lamp")
                        && source.contains("list.add(new ItemStack(this, 1, 9)); // alchemical construct")
                        && source.contains("list.add(new ItemStack(this, 1, 3)); // advanced alchemical construct")
                        && !source.contains("list.add(new ItemStack(this, 1, 10)); // thaumatorium")
                        && !source.contains("list.add(new ItemStack(this, 1, 11)); // thaumatorium top"));

        assertTrue("tile routing should keep only the real tile metas and return null for support-only construct states",
                source.contains("return meta == 0 || meta == 1 || meta == 2 || meta == 5 || meta == 6")
                        && source.contains("|| meta == 7 || meta == 8 || meta == 10 || meta == 11 || meta == 12")
                        && source.contains("|| meta == 13 || meta == 14;")
                        && source.contains("if (meta == 10) return new TileThaumatorium();")
                        && source.contains("if (meta == 11) return new TileThaumatoriumTop();")
                        && source.contains("return null;"));

        assertTrue("drop remap should keep closed-grate and thaumatorium fallback metadata behavior",
                source.contains("if (meta == 6) return 5;")
                        && source.contains("if (meta == 10 || meta == 11) return 9;"));

        assertTrue("alembic placement and thaumatorium support checks should keep the reference orientation and fallback paths",
                source.contains("if (state.getValue(TYPE) == 1) {")
                        && source.contains("((TileAlembic) te).facing = placer.getHorizontalFacing().getOpposite().getIndex();")
                        && source.contains("if (meta == 10) {")
                        && source.contains("above.getBlock() != this || above.getValue(TYPE) != 11")
                        && source.contains("below.getBlock() != this || below.getValue(TYPE) != 0")
                        && source.contains("state.withProperty(TYPE, 9)")
                        && source.contains("((TileThaumatorium) te).getUpgrades();")
                        && source.contains("if (meta == 11) {"));

        assertTrue("special bounds should cover charger, thaumatorium halves, brainbox, and orientation-aware vis relay shells",
                source.contains("private static final AxisAlignedBB CHARGER_AABB")
                        && source.contains("private static final AxisAlignedBB THAUMATORIUM_BASE_AABB")
                        && source.contains("private static final AxisAlignedBB THAUMATORIUM_TOP_AABB")
                        && source.contains("private static final AxisAlignedBB BRAINBOX_AABB")
                        && source.contains("private AxisAlignedBB getVisRelayBounds(IBlockAccess source, BlockPos pos)")
                        && source.contains("if (meta == 2) {")
                        && source.contains("return CHARGER_AABB;")
                        && source.contains("if (meta == 10) {")
                        && source.contains("return THAUMATORIUM_BASE_AABB;")
                        && source.contains("if (meta == 11) {")
                        && source.contains("return THAUMATORIUM_TOP_AABB;")
                        && source.contains("if (meta == 12) {")
                        && source.contains("return BRAINBOX_AABB;")
                        && source.contains("if (meta == 14) {")
                        && source.contains("return getVisRelayBounds(source, pos);"));

        assertTrue("light and comparator contracts should keep advanced construct glow plus alembic/crucible/thaumatorium redstone output",
                source.contains("if (meta == 3) {")
                        && source.contains("return 11;")
                        && source.contains("public boolean hasComparatorInputOverride(IBlockState state) {")
                        && source.contains("return Container.calcRedstoneFromInventory((IInventory) te);")
                        && source.contains("float fill = (float) ((TileAlembic) te).amount / (float) ((TileAlembic) te).maxAmount;")
                        && source.contains("float fill = ((TileCrucible) te).aspects.visSize() / 100.0F;"));

        assertTrue("blockstate routing should keep the construct family off the old pedestal placeholder",
                blockstate.contains("\"type=3\": { \"model\": \"thaumcraft:blockmetaldevice_3\" }")
                        && blockstate.contains("\"type=4\": { \"model\": \"thaumcraft:blockmetaldevice_9\" }")
                        && blockstate.contains("\"type=9\": { \"model\": \"thaumcraft:blockmetaldevice_9\" }")
                        && !blockstate.contains("\"type=3\": { \"model\": \"thaumcraft:blockmetaldevice_pedestal\" }")
                        && !blockstate.contains("\"type=9\": { \"model\": \"thaumcraft:blockmetaldevice_pedestal\" }"));

        assertTrue("construct-family fallback models should retain the reference alchemy textures",
                advancedConstructModel.contains("\"all\": \"thaumcraft:blocks/alchemyblockadv\"")
                        && constructModel.contains("\"all\": \"thaumcraft:blocks/alchemyblock\""));
    }

    @Test
    public void crucibleCollisionShouldKeepTc4HollowBasinForDroppedItems() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");

        assertTrue("crucible metadata 0 should keep the TC4 hollow basin collision shape",
                source.contains("private static final AxisAlignedBB CRUCIBLE_BOTTOM_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);")
                        && source.contains("private static final AxisAlignedBB CRUCIBLE_WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 0.85D, 1.0D);")
                        && source.contains("private static final AxisAlignedBB CRUCIBLE_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.85D, 0.125D);")
                        && source.contains("private static final AxisAlignedBB CRUCIBLE_EAST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 0.85D, 1.0D);")
                        && source.contains("private static final AxisAlignedBB CRUCIBLE_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 0.85D, 1.0D);")
                        && source.contains("if (meta == 0) {")
                        && source.contains("addCrucibleCollisionBoxes(pos, entityBox, collidingBoxes);")
                        && source.contains("addCollisionBoxToList(pos, entityBox, collidingBoxes, CRUCIBLE_BOTTOM_AABB);")
                        && source.contains("addCollisionBoxToList(pos, entityBox, collidingBoxes, CRUCIBLE_WEST_AABB);")
                        && source.contains("addCollisionBoxToList(pos, entityBox, collidingBoxes, CRUCIBLE_NORTH_AABB);")
                        && source.contains("addCollisionBoxToList(pos, entityBox, collidingBoxes, CRUCIBLE_EAST_AABB);")
                        && source.contains("addCollisionBoxToList(pos, entityBox, collidingBoxes, CRUCIBLE_SOUTH_AABB);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
