
package thaumcraft.common.lib.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUtils {

    public static boolean isBlockBreakable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().getBlockHardness(state, world, pos) >= 0.0f;
    }

    public static void setBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 3);
    }

    public static ItemStack createStackedBlock(Block block, int meta) {
        if (block == null) return ItemStack.EMPTY;
        Item item = Item.getItemFromBlock(block);
        if (item == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(item, 1, meta);
    }

    public static boolean isBlockExposed(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(facing))) return true;
        }
        return false;
    }

    public static void dropBlockAsItem(World world, int x, int y, int z, ItemStack stack, Block block) {
        if (world == null || world.isRemote || stack == null || stack.isEmpty()) return;
        Block.spawnAsEntity(world, new BlockPos(x, y, z), stack);
    }

    public static void dropBlockAsItemWithChance(World world, Block block, int x, int y, int z, int meta, float chance, int fortune, EntityPlayer player) {
        if (world == null || world.isRemote || block == null) return;
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        NonNullList<ItemStack> drops = NonNullList.create();
        block.getDrops(drops, world, pos, state, fortune);
        float actualChance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, chance, false, player);
        for (ItemStack drop : drops) {
            if (drop.isEmpty() || world.rand.nextFloat() > actualChance) continue;
            EntityItem entity = new EntityItem(world, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, drop);
            entity.setDefaultPickupDelay();
            world.spawnEntity(entity);
        }
    }

    /** Count how many sides of a block are exposed to air. */
    public static int countExposedSides(World world, int x, int y, int z) {
        int count = 0;
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(facing))) count++;
        }
        return count;
    }

    /** Check if a block has at least `count` adjacent blocks of the given type. */
    public static boolean isBlockAdjacentToAtleast(IBlockAccess world, int x, int y, int z, Block block, int maxMeta, int count) {
        int found = 0;
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == block) {
                found++;
                if (found >= count) return true;
            }
        }
        return false;
    }

    /** Check if a block position is adjacent to any solid (full-cube) block. */
    public static boolean isAdjacentToSolidBlock(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos sidePos = pos.offset(facing);
            IBlockState state = world.getBlockState(sidePos);
            if (state.isSideSolid(world, sidePos, facing.getOpposite())) return true;
        }
        return false;
    }

    /** Break the furthest connected block of the given type from the start position. Used by lumber/harvest golems. */
    public static boolean breakFurthestBlock(World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
        // Simplified: break the block at the given position
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isAir(state, world, pos)) return false;
        if (player != null && !world.isRemote) {
            state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), net.minecraft.item.ItemStack.EMPTY);
            world.setBlockToAir(pos);
            return true;
        }
        if (!world.isRemote) {
            world.destroyBlock(pos, true);
            return true;
        }
        return false;
    }
}
