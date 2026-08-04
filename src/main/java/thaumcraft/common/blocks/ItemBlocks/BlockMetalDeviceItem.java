package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockMetalDevice;
import thaumcraft.common.tiles.TileArcaneLamp;
import thaumcraft.common.tiles.TileArcaneLampFertility;
import thaumcraft.common.tiles.TileArcaneLampGrowth;

public class BlockMetalDeviceItem extends BlockMetadataItem {
    public BlockMetalDeviceItem(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            return false;
        }

        int metadata = newState.getValue(BlockMetalDevice.TYPE);
        TileEntity tile = world.getTileEntity(pos);
        EnumFacing support = side.getOpposite();
        if (metadata == 7 && tile instanceof TileArcaneLamp) {
            ((TileArcaneLamp) tile).facing = support;
        } else if (metadata == 8 && tile instanceof TileArcaneLampGrowth) {
            ((TileArcaneLampGrowth) tile).facing = support;
        } else if (metadata == 13 && tile instanceof TileArcaneLampFertility) {
            ((TileArcaneLampFertility) tile).facing = support;
        } else {
            return true;
        }
        tile.markDirty();
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        return true;
    }
}
