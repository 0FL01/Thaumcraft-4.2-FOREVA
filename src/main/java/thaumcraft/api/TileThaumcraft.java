package thaumcraft.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileThaumcraft
extends TileEntity {
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.readCustomNBT(nbttagcompound);
    }

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        NBTTagCompound ret = super.writeToNBT(nbttagcompound);
        this.writeCustomNBT(ret);
        return ret;
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeCustomNBT(nbttagcompound);
        return new SPacketUpdateTileEntity(this.getPos(), -999, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.readCustomNBT(pkt.getNbtCompound());
    }
}
