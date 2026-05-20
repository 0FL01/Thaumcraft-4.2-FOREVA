package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import thaumcraft.common.blocks.BlockCosmeticSolid;
import thaumcraft.common.config.ConfigBlocks;

public class TileWardingStoneFence extends TileEntity implements ITickable {
    private int count = 0;

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) return;

        if (this.count == 0) {
            this.count = this.world.rand.nextInt(100);
        }

        if (++this.count % 100 == 0) {
            boolean supportedByBase = this.world.getBlockState(this.pos.down()).getBlock() == ConfigBlocks.blockCosmeticSolid
                    && this.world.getBlockState(this.pos.down()).getValue(BlockCosmeticSolid.TYPE) == 3;
            boolean supportedByLowerBase = this.world.getBlockState(this.pos.down(2)).getBlock() == ConfigBlocks.blockCosmeticSolid
                    && this.world.getBlockState(this.pos.down(2)).getValue(BlockCosmeticSolid.TYPE) == 3;
            if (!supportedByBase && !supportedByLowerBase) {
                this.world.setBlockToAir(this.pos);
            }
        }
    }
}
