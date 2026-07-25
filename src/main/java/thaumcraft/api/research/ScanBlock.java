package thaumcraft.api.research;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ScanBlock implements IScanThing {
    private final String research;
    private final Block[] blocks;

    public ScanBlock(Block block) {
        this("!" + block.getRegistryName(), block);
    }

    public ScanBlock(String research, Block... blocks) {
        this.research = research;
        this.blocks = blocks;
    }

    @Override
    public boolean checkThing(EntityPlayer player, Object thing) {
        if (!(thing instanceof BlockPos)) {
            return false;
        }
        Block found = player.world.getBlockState((BlockPos) thing).getBlock();
        for (Block block : blocks) {
            if (found == block) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getResearchKey(EntityPlayer player, Object thing) {
        return research;
    }
}
