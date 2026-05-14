package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.tiles.TileArcaneBore;
import thaumcraft.common.tiles.TileArcaneBoreBase;
import thaumcraft.common.tiles.TileBellows;

public class BlockWoodenDeviceItem extends BlockMetadataItem {
    public BlockWoodenDeviceItem(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (!placed) return false;

        TileEntity tile = world.getTileEntity(pos);
        int metadata = stack.getItemDamage();
        if (metadata == 0 && tile instanceof TileBellows) {
            EnumFacing out = side.getOpposite();
            TileBellows bellows = (TileBellows) tile;
            bellows.orientation = (byte) out.getIndex();
            bellows.onVanillaFurnace = world.getBlockState(pos.offset(out)).getBlock() == Blocks.FURNACE
                    || world.getBlockState(pos.offset(out)).getBlock() == Blocks.LIT_FURNACE;
            bellows.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        } else if (metadata == 4 && tile instanceof TileArcaneBoreBase) {
            ((TileArcaneBoreBase) tile).orientation = player == null ? EnumFacing.NORTH : player.getHorizontalFacing().getOpposite();
            tile.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        } else if (metadata == 5 && tile instanceof TileArcaneBore) {
            TileArcaneBore bore = (TileArcaneBore) tile;
            bore.baseOrientation = side;
            if (player != null) {
                int facing = MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
                switch (facing) {
                    case 0: bore.orientation = EnumFacing.SOUTH; break;
                    case 1: bore.orientation = EnumFacing.WEST; break;
                    case 2: bore.orientation = EnumFacing.NORTH; break;
                    case 3: bore.orientation = EnumFacing.EAST; break;
                    default: bore.orientation = EnumFacing.UP; break;
                }
            }
            bore.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return true;
    }
}
