package thaumcraft.common.lib.world;

import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileManaPod;

public class WorldGenManaPods extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int baseX = pos.getX();
        int baseZ = pos.getZ();
        int x = baseX;
        int z = baseZ;
        int y = pos.getY();

        while (y < Math.min(128, world.getHeight(new BlockPos(x, 0, z)).getY())) {
            BlockPos column = new BlockPos(x, 0, z);
            if (!world.isBlockLoaded(column, false)) {
                x = baseX + rand.nextInt(4) - rand.nextInt(4);
                z = baseZ + rand.nextInt(4) - rand.nextInt(4);
                y++;
                continue;
            }

            BlockPos bp = new BlockPos(x, y, z);
            if (!world.isAreaLoaded(bp.down(), bp.up(), false)) {
                y++;
                continue;
            }

            if (world.isAirBlock(bp) && world.isAirBlock(bp.down())) {
                if (ConfigBlocks.blockManaPod.canPlaceBlockOnSide(world, bp, EnumFacing.DOWN)) {
                    world.setBlockState(bp, ConfigBlocks.blockManaPod.getStateFromMeta(2 + rand.nextInt(5)), 2);
                    TileEntity tile = world.getTileEntity(bp);
                    if (tile instanceof TileManaPod) {
                        ((TileManaPod) tile).checkGrowth();
                    }
                    break;
                }
            } else {
                x = baseX + rand.nextInt(4) - rand.nextInt(4);
                z = baseZ + rand.nextInt(4) - rand.nextInt(4);
            }

            y++;
        }
        return true;
    }
}
