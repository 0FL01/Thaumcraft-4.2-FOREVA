package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTaintFibres;

public class BlockTaintFibresItem extends ItemBlock {
    public BlockTaintFibresItem(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState newState) {
        if (newState.getValue(BlockTaintFibres.TYPE) != 0 && !hasSolidSupport(world, pos)) {
            return false;
        }
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        if (this.block.getStateFromMeta(stack.getItemDamage()).getValue(BlockTaintFibres.TYPE) != 0) {
            Block clickedBlock = world.getBlockState(pos).getBlock();
            BlockPos placementPos = pos;
            if (!clickedBlock.isReplaceable(world, pos)) {
                placementPos = pos.offset(side);
            }
            if (!hasSolidSupport(world, placementPos)) {
                return false;
            }
        }
        return super.canPlaceBlockOnSide(world, pos, side, player, stack);
    }

    private static boolean hasSolidSupport(World world, BlockPos pos) {
        BlockPos supportPos = pos.down();
        return world.getBlockState(supportPos).isSideSolid(world, supportPos, EnumFacing.UP);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }
}
