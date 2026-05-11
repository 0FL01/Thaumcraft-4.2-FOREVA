package thaumcraft.common.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.common.tiles.TilePedestal;

public class TileWandPedestal
extends TilePedestal {

    public ItemStack focus = ItemStack.EMPTY;

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        if (compound.hasKey("focus")) {
            this.focus = new ItemStack(compound.getCompoundTag("focus"));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        if (!this.focus.isEmpty()) {
            NBTTagCompound tag = new NBTTagCompound();
            this.focus.writeToNBT(tag);
            compound.setTag("focus", tag);
        }
    }
}
