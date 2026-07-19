
package thaumcraft.common.lib.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nullable;

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

    /** Check the surrounding 3x3x3 cube for at least `count` matching blocks. */
    public static boolean isBlockAdjacentToAtleast(IBlockAccess world, int x, int y, int z, Block block, int maxMeta, int count) {
        int found = 0;
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    if (xx == 0 && yy == 0 && zz == 0) continue;
                    IBlockState state = world.getBlockState(new BlockPos(x + xx, y + yy, z + zz));
                    if (state.getBlock() == block
                            && (maxMeta == Short.MAX_VALUE || block.getMetaFromState(state) == maxMeta)) {
                        found++;
                        if (found >= count) return true;
                    }
                }
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

    public static boolean harvestBlock(World world, BlockPos pos, @Nullable EntityPlayer player) {
        if (world == null || world.isRemote) return false;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block.isAir(state, world, pos) || state.getBlockHardness(world, pos) < 0.0F) return false;
        if (player == null) return world.destroyBlock(pos, true);

        int xp = 0;
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
            if (serverPlayer instanceof FakePlayer) {
                BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, serverPlayer);
                MinecraftForge.EVENT_BUS.post(event);
                xp = event.isCanceled() ? -1 : event.getExpToDrop();
            } else {
                xp = ForgeHooks.onBlockBreakEvent(world, serverPlayer.interactionManager.getGameType(), serverPlayer, pos);
            }
            if (xp < 0) return false;
        }

        boolean creative = player.capabilities.isCreativeMode;
        boolean canHarvest = !creative && block.canHarvestBlock(world, pos, player);
        TileEntity tile = world.getTileEntity(pos);
        ItemStack tool = player.getHeldItemMainhand();
        world.playEvent(2001, pos, Block.getStateId(state));
        block.onBlockHarvested(world, pos, state, player);
        if (!block.removedByPlayer(state, world, pos, player, canHarvest)) return false;
        block.onPlayerDestroy(world, pos, state);
        if (canHarvest) block.harvestBlock(world, player, pos, state, tile, tool);
        if (!creative && xp > 0) block.dropXpOnBlockBreak(world, pos, xp);
        return true;
    }

    public static void destroyBlockPartially(World world, int breakerId, BlockPos pos, int progress) {
        if (world != null && !world.isRemote) world.sendBlockBreakProgress(breakerId, pos, progress);
    }

    /** Break the furthest connected block of the given type from the start position. Used by lumber/harvest golems. */
    public static boolean breakFurthestBlock(World world, BlockPos pos, @Nullable EntityPlayer player) {
        if (world == null || world.isRemote) return false;
        IBlockState originState = world.getBlockState(pos);
        Block originBlock = originState.getBlock();
        if (originBlock.isAir(originState, world, pos)) return false;

        BlockPos current = pos;
        double lastDistance = 0.0D;
        while (true) {
            BlockPos next = null;
            search:
            for (int xx = -2; xx <= 2; xx++) {
                for (int yy = 2; yy >= -2; yy--) {
                    for (int zz = -2; zz <= 2; zz++) {
                        BlockPos candidate = current.add(xx, yy, zz);
                        if (Math.abs(candidate.getX() - pos.getX()) > 24
                                || Math.abs(candidate.getY() - pos.getY()) > 48
                                || Math.abs(candidate.getZ() - pos.getZ()) > 24) continue;
                        double distance = candidate.distanceSq(pos);
                        IBlockState candidateState = world.getBlockState(candidate);
                        if (distance > lastDistance
                                && candidateState.getBlock() == originBlock
                                && Utils.isWoodLog(world, candidate)
                                && candidateState.getBlockHardness(world, candidate) >= 0.0F) {
                            next = candidate;
                            lastDistance = distance;
                            break search;
                        }
                    }
                }
            }
            if (next == null) break;
            current = next;
        }

        if (!harvestBlock(world, current, player)) return false;
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.markBlockRangeForRenderUpdate(current, current);
        for (int xx = -3; xx <= 3; xx++) {
            for (int yy = -3; yy <= 3; yy++) {
                for (int zz = -3; zz <= 3; zz++) {
                    BlockPos updatePos = current.add(xx, yy, zz);
                    world.scheduleUpdate(updatePos, world.getBlockState(updatePos).getBlock(), 150 + world.rand.nextInt(150));
                }
            }
        }
        return true;
    }
}
