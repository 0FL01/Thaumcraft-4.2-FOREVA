package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;

final class NodeStabilizerHelper {
    private NodeStabilizerHelper() {
    }

    static byte getActiveLock(World world, BlockPos stabilizerPos, boolean requireTile) {
        if (world == null || stabilizerPos == null || world.isBlockPowered(stabilizerPos)) {
            return 0;
        }
        IBlockState state = world.getBlockState(stabilizerPos);
        if (state.getBlock() != ConfigBlocks.blockStoneDevice
                || requireTile && !(world.getTileEntity(stabilizerPos) instanceof TileNodeStabilizer)) {
            return 0;
        }
        int meta = state.getBlock().getMetaFromState(state);
        return meta == 9 ? (byte) 1 : meta == 10 ? (byte) 2 : 0;
    }
}
