package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import thaumcraft.api.TileThaumcraft;

public class TileBrainbox extends TileThaumcraft {
    public EnumFacing facing = EnumFacing.NORTH;

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.facing = EnumFacing.byIndex(nbt.getByte("facing"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("facing", (byte) this.facing.getIndex());
    }
}
