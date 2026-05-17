package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.TileThaumcraft;

public class TileInfusionPillar extends TileThaumcraft {
    public byte orientation = 0;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.pos.getX() - 1,
                this.pos.getY() - 1,
                this.pos.getZ() - 1,
                this.pos.getX() + 1,
                this.pos.getY() + 2,
                this.pos.getZ() + 1);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = nbt.getByte("orientation");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("orientation", this.orientation);
    }
}
