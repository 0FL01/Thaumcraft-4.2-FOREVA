package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockArcaneFurnaceStaticGuardTest {

    @Test
    public void blockArcaneFurnaceShouldKeepMetadataTileAndCollisionContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockArcaneFurnace.java");

        assertTrue(source.contains("public static final PropertyInteger TYPE = PropertyInteger.create(\"type\", 0, 10);"));
        assertTrue(source.contains("public static final PropertyDirection FACING = PropertyDirection.create(\"facing\", EnumFacing.Plane.HORIZONTAL);"));
        assertTrue(source.contains("return meta == 0 || meta == 2 || meta == 4 || meta == 5 || meta == 6 || meta == 8;"));
        assertTrue(source.contains("if (meta == 0) {"));
        assertTrue(source.contains("return new TileArcaneFurnace();"));
        assertTrue(source.contains("return new TileArcaneFurnaceNozzle();"));
        assertTrue(source.contains("if (this.getMetaFromState(state) != 10) {"));
        assertTrue(source.contains("return state.withProperty(FACING, EnumFacing.NORTH);"));
        assertTrue(source.contains("return state.withProperty(FACING, this.getNozzleFacing(worldIn, pos));"));
        assertTrue(source.contains("if (meta == 0 || meta == 10) {"));
        assertTrue(source.contains("return 13;"));
        assertTrue(source.contains("return CORE_AABB;"));
        assertTrue(source.contains("if (meta == 10) {"));
        assertTrue(source.contains("return this.getNozzleBounds(this.getNozzleFacing(worldIn, pos));"));
        assertTrue(source.contains("return this.getBoundingBox(state, worldIn, pos).offset(pos);"));
        assertTrue(source.contains("return new BlockStateContainer(this, TYPE, FACING);"));
        assertTrue(source.contains("withProperty(FACING, EnumFacing.NORTH);"));
        assertTrue(source.contains("this.getMetaFromState(world.getBlockState(pos.west())) == 0"));
        assertTrue(source.contains("this.getMetaFromState(world.getBlockState(pos.east())) == 0"));
        assertTrue(source.contains("this.getMetaFromState(world.getBlockState(pos.north())) == 0"));
        assertTrue(source.contains("return EnumFacing.SOUTH;"));
        assertTrue(source.contains("entityIn instanceof EntityItem"));
        assertTrue(source.contains("((TileArcaneFurnace) tile).addItemsToInventory(stack.copy())"));
        assertTrue(source.contains("entityIn.attackEntityFrom(net.minecraft.util.DamageSource.HOT_FLOOR, 3.0F);"));
        assertTrue(source.contains("entityIn.setFire(10);"));
    }

    @Test
    public void blockArcaneFurnaceShouldKeepRestoreDropAndBlazeContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockArcaneFurnace.java");

        assertTrue(source.contains("public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)"));
        assertTrue(source.contains("IBlockState restoredState = this.getRestoredState(this.getMetaFromState(state));"));
        assertTrue(source.contains("if (restoredState.getBlock() != Blocks.AIR) {"));
        assertTrue(source.contains("restoredState.getBlock().damageDropped(restoredState)"));
        assertTrue(source.contains("public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, net.minecraft.entity.player.EntityPlayer player)"));
        assertTrue(source.contains("new EntityBlaze(worldIn);"));
        assertTrue(source.contains("new PotionEffect(MobEffects.RESISTANCE, 6000, 2, false, true)"));
        assertTrue(source.contains("new PotionEffect(MobEffects.FIRE_RESISTANCE, 12000, 0, false, true)"));
        assertTrue(source.contains("public void breakBlock(World worldIn, BlockPos pos, IBlockState state)"));
        assertTrue(source.contains("if (!worldIn.isRemote && this.getMetaFromState(state) == 0) {"));
        assertTrue(source.contains("public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos)"));
        assertTrue(source.contains("this.isArcaneFurnaceBroken(worldIn, pos)"));
        assertTrue(source.contains("private void restoreBlocks(World worldIn, BlockPos pos)"));
        assertTrue(source.contains("if ((yy == 0 && xx == 0 && zz == 0) || (yy == 1 && xx == 0 && zz == 0)) {"));
        assertTrue(source.contains("Blocks.AIR.getDefaultState()"));
        assertTrue(source.contains("Blocks.NETHER_BRICK_FENCE.getDefaultState()"));
        assertTrue(source.contains("Blocks.OBSIDIAN.getDefaultState()"));
        assertTrue(source.contains("Blocks.NETHERRACK.getDefaultState()"));
    }

    @Test
    public void configBlocksShouldRegisterBlockArcaneFurnaceAndItem() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");

        assertTrue(source.contains("public static BlockArcaneFurnace blockArcaneFurnace;"));
        assertTrue(source.contains("blockArcaneFurnace = (BlockArcaneFurnace) new BlockArcaneFurnace()"));
        assertTrue(source.contains("legacyPath(\"blockArcaneFurnace\")"));
        assertTrue(source.contains("blockArcaneFurnace,"));
        assertTrue(source.contains("new BlockMetadataItem(blockArcaneFurnace)"));
        assertTrue(source.contains("setRegistryName(blockArcaneFurnace.getRegistryName())"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
