package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;

public class TileTubeValve extends TileTube {
    public boolean allowFlow = true;
    private boolean wasPoweredLastTick = false;

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote && this.count % 5 == 0) {
            boolean powered = this.world.isBlockPowered(this.pos);
            if (powered != this.wasPoweredLastTick) {
                this.allowFlow = !powered;
                this.markDirtyAndSync();
            }
            this.wasPoweredLastTick = powered;
        }
        super.update();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        this.allowFlow = !nbt.hasKey("flow") || nbt.getBoolean("flow");
        this.wasPoweredLastTick = nbt.getBoolean("hadpower");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setBoolean("flow", this.allowFlow);
        nbt.setBoolean("hadpower", this.wasPoweredLastTick);
    }

    @Override
    public boolean isConnectable(net.minecraft.util.EnumFacing face) {
        return face != this.facing && super.isConnectable(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        if (this.allowFlow) super.setSuction(aspect, amount);
    }

    @Override
    public boolean canInputFrom(net.minecraft.util.EnumFacing face) { return this.allowFlow && super.canInputFrom(face); }

    @Override
    public boolean canOutputTo(net.minecraft.util.EnumFacing face) { return this.allowFlow && super.canOutputTo(face); }
}
