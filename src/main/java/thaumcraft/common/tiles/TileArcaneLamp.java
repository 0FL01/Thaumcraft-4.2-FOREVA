package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;

public class TileArcaneLamp extends TileThaumcraft implements ITickable {
    public EnumFacing facing = EnumFacing.byIndex(0);

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        int x = this.pos.getX() + this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
        int z = this.pos.getZ() + this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
        int y = this.pos.getY() + this.world.rand.nextInt(16) - this.world.rand.nextInt(16);

        int maxY = this.world.getHeight(new BlockPos(x, 0, z)).getY() + 4;
        if (y > maxY) {
            y = maxY;
        }
        if (y < 5) {
            y = 5;
        }

        BlockPos target = new BlockPos(x, y, z);
        if (this.world.isAirBlock(target)
                && this.world.getBlockState(target).getBlock() != ConfigBlocks.blockAiry
                && this.world.getLight(target) < 9) {
            this.world.setBlockState(target,
                    ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 3), 3);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.facing = EnumFacing.byIndex(nbt.getInteger("orientation"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("orientation", this.facing.getIndex());
    }

    public void removeLights() {
        if (this.world == null || this.pos == null) {
            return;
        }
        for (int x = -15; x <= 15; x++) {
            for (int y = -15; y <= 15; y++) {
                for (int z = -15; z <= 15; z++) {
                    BlockPos check = this.pos.add(x, y, z);
                    if (this.world.getBlockState(check).getBlock() == ConfigBlocks.blockAiry
                            && this.world.getBlockState(check).getValue(BlockAiry.TYPE) == 3) {
                        this.world.setBlockToAir(check);
                    }
                }
            }
        }
    }
}
